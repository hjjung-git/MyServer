# Deployment Guide

> **현재 상태**: 아래 절차는 서버 구축~서비스 확장 단계 기준(AWS EC2 + Nginx) 운영 방식이다. 아키텍처 마이그레이션 단계에서 자체 소유 하드웨어 + Cloudflare Tunnel 방식으로 전환 예정이며, 전환이 끝나면 이 문서를 갱신한다. 진행 배경은 [docs/architecture-migration-log.md](architecture-migration-log.md) 참고.

---

## 로컬 실행

```bash
cd my-server
mvn spring-boot:run
```

접속 → http://localhost:8081

<br>

---

## 운영 서버 배포 (AWS EC2)

> main 브랜치에 push하면 GitHub Actions가 자동으로 빌드 → 배포 → 재시작합니다.  
> 아래 수동 배포 절차는 최초 설정 또는 긴급 상황에만 사용합니다.

### 1. 빌드

```bash
cd my-server
mvn clean package -DskipTests
```

`target/my-server-0.0.1-SNAPSHOT.jar` 생성 확인

<br>

### 2. 서버 전송

```bash
scp -i [your-key.pem] target/my-server-0.0.1-SNAPSHOT.jar ec2-user@[EC2_PUBLIC_IP]:~/
```

<br>

### 3. SSH 접속

```bash
ssh -i [your-key.pem] ec2-user@[EC2_PUBLIC_IP]
```

<br>

### 4. 실행

```bash
# 백그라운드 (nohup - 레거시 방식)
nohup java -jar my-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod &

# systemd 등록 후 사용 권장
sudo systemctl start my-server
```

<br>

---

## systemd 서비스 등록 (최초 1회)

EC2 재부팅 시 Spring Boot 애플리케이션이 자동으로 실행되도록 설정합니다.

### 1. 서비스 파일 생성

```bash
sudo vi /etc/systemd/system/my-server.service
```

아래 내용 입력

```ini
[Unit]
Description=My Server Spring Boot Application
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user
Environment=DATABASE_URL=jdbc:mysql://[RDS_ENDPOINT]:3306/[DB_NAME]
Environment=DATABASE_USERNAME=[DB_USERNAME]
Environment=DATABASE_PASSWORD=[DB_PASSWORD]
ExecStart=/usr/bin/java -jar /home/ec2-user/my-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

### 2. 서비스 등록 및 시작

```bash
sudo systemctl daemon-reload
sudo systemctl enable my-server    # 부팅 시 자동 시작 등록
sudo systemctl start my-server     # 즉시 시작
```

> 서비스 파일에는 DB 비밀번호 등 민감정보가 포함되어 있으므로 권한을 제한한다.
> ```bash
> sudo chmod 600 /etc/systemd/system/my-server.service
> ```

### 3. 상태 확인

```bash
sudo systemctl status my-server
```

### 4. 로그 확인

```bash
# 실시간 로그
sudo journalctl -u my-server -f

# 최근 100줄
sudo journalctl -u my-server -n 100
```

<br>

---

## GitHub Actions Secrets 등록 (최초 1회)

GitHub 저장소 → Settings → Secrets and variables → Actions → New repository secret

| Secret 이름 | 값 |
| :--- | :--- |
| `EC2_HOST` | EC2 퍼블릭 IP 또는 도메인 |
| `EC2_USER` | `ec2-user` |
| `EC2_SSH_KEY` | `.pem` 파일 전체 내용 (`-----BEGIN RSA PRIVATE KEY-----` 포함) |

<br>

---

## Nginx

### 설정 파일

경로 : `/etc/nginx/conf.d/my-server.conf`

```nginx
server {
    listen 80;
    server_name [MY_DOMAIN];

    client_max_body_size 10M;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

<br>

### 명령어

```bash
sudo systemctl start nginx     # 시작
sudo systemctl restart nginx   # 재시작 (설정 적용)
sudo nginx -t                  # 설정 문법 검사
```

<br>

---

## SSL 인증서 (Let's Encrypt)

```bash
sudo dnf install -y certbot python3-certbot-nginx
sudo certbot --nginx -d [MY_DOMAIN]
```

자동 갱신 확인

```bash
sudo certbot renew --dry-run
```

<br>

---

## AWS 보안 그룹 인바운드 규칙

| 유형 | 포트 | 소스 |
| :--- | :---: | :--- |
| SSH | 22 | 0.0.0.0/0 |
| HTTP | 80 | 0.0.0.0/0 |
| HTTPS | 443 | 0.0.0.0/0 |
| MySQL/Aurora | 3306 | 내 IP + EC2 보안 그룹 |
