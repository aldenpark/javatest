# 🛍️ Simulated Stack (Free-Tier Aligned)

| Component            | Tech                    | Free-Tier Safe                           |
|---------------------|-------------------------|-------------------------------------------|
| Java Microservice   | Spring Boot + Docker    | ✅ Yes                                     |
| GraphQL Gateway     | Node.js + Docker        | ✅ Yes                                     |
| Event Messaging     | AWS SNS + SQS           | ✅ Yes                                     |
| Database            | RDS PostgreSQL/MySQL    | ✅ Yes (750 hrs/month, `db.t3.micro`)      |
| Container Deployment| ECS Fargate             | ✅ Yes (0.5 vCPU, 1 GB memory)             |
| Container Registry  | ECR                     | ✅ Yes (500 MB storage)                    |

---

# 🚀 Step-by-Step Setup (Free Tier)

> ⚠️ **Note:** For dev use only — **NOT** safe for production.

## ✅ Recommended Directory Layout

```plaintext
javatest-project/
├── javatestapi/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── graphql-gateway/
│   ├── Dockerfile
│   ├── package.json
│   └── src/
└── docker-compose.yml  # optional for local dev
```

---

## 📦 Step 1: Set Up AWS Environment

### Prerequisites (Local Dev)
- AWS CLI
- Docker
- Java 17 (e.g., OpenJDK)
- Node.js (for GraphQL)

```bash
aws configure --profile javatest-local
# Use us-west-2 (Oregon)
```

---

## 👤 Step 2: Create IAM User & Roles

- IAM User: `javatest-local`
- ECS Task Execution Role permissions:
  - `AmazonECSTaskExecutionRolePolicy`
  - `AmazonSQSFullAccess`
  - `AmazonSNSFullAccess`

---

## 📂 Step 3: Create RDS PostgreSQL Database

- Engine: PostgreSQL
- Template: Free Tier
- Instance class: `db.t3.micro`
- Storage: 20 GB
- Public access: Yes (for dev)
- Store credentials in **AWS Secrets Manager**

Save:
- DB endpoint
- Username/password

---

## 📬 Step 4: Create SNS & SQS

```bash
aws sns create-topic --name javatest-topic --region us-west-2
aws sqs create-queue --queue-name javatest-queue --region us-west-2
```

Subscribe SQS to SNS:

```bash
aws sns subscribe \
  --topic-arn arn:aws:sns:us-west-2:<acct>:javatest-topic \
  --protocol sqs \
  --notification-endpoint arn:aws:sqs:us-west-2:<acct>:javatest-queue
```

---

## ☕ Step 5: Build Java Microservice

**Dependencies (pom.xml)**

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-aws-messaging</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**Dockerfile**

```dockerfile
FROM eclipse-temurin:17-jdk
COPY target/javatestapi.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080
```

**Build & Test**

```bash
mvn clean package
docker build -t javatestapi .
java -jar target/javatestapi-0.0.1-SNAPSHOT.jar
```

---

## 🌐 Step 6: Build GraphQL Gateway

**Dockerfile**

```dockerfile
FROM node:20
WORKDIR /app
COPY . .
RUN npm install
CMD ["npm", "start"]
EXPOSE 4000
```

**Build & Test**

```bash
docker build -t graphql-gateway .
node src/index.js
```

---

## 🐳 Step 7: Push Docker Images to ECR

```bash
aws ecr create-repository --repository-name javatestapi
aws ecr create-repository --repository-name graphql-gateway

# Auth
docker login --username AWS --password-stdin <acct>.dkr.ecr.us-west-2.amazonaws.com <<< $(aws ecr get-login-password --region us-west-2)

# Build & Push
docker tag javatestapi:latest <acct>.dkr.ecr.us-west-2.amazonaws.com/javatestapi:latest
docker push <acct>.dkr.ecr.us-west-2.amazonaws.com/javatestapi:latest

docker tag graphql-gateway:latest <acct>.dkr.ecr.us-west-2.amazonaws.com/graphql-gateway:latest
docker push <acct>.dkr.ecr.us-west-2.amazonaws.com/graphql-gateway:latest
```

---

## 🚢 Step 8: Deploy to ECS Fargate

### Create ECS Roles

```bash
aws iam create-service-linked-role --aws-service-name ecs.amazonaws.com

aws iam create-role --role-name ecsTaskRole --assume-role-policy-document file://<(cat <<EOF
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": {"Service": "ecs-tasks.amazonaws.com"},
    "Action": "sts:AssumeRole"
  }]
}
EOF
)

aws iam attach-role-policy --role-name ecsTaskRole --policy-arn arn:aws:iam::aws:policy/AmazonSNSFullAccess
aws iam attach-role-policy --role-name ecsTaskRole --policy-arn arn:aws:iam::aws:policy/AmazonSQSFullAccess
```

### Create ECS Cluster

```bash
aws ecs create-cluster \
  --cluster-name javatest-cluster \
  --capacity-providers FARGATE \
  --region us-west-2
```

### Task Definitions (Console)
- Launch Type: Fargate
- Roles: `ecsTaskRole`, `ecsTaskExecutionRole`
- Port Mappings: `8080` (Java) / `4000` (GraphQL)

---

## 🔐 Environment Variables Example

**javatestapi**

| Key           | Example Value                           |
|---------------|------------------------------------------|
| DB_URL        | `jdbc:mysql://<rds-endpoint>:3306/mydb` |
| SNS_TOPIC_ARN | `arn:aws:sns:us-west-2:...`              |

**graphql-gateway**

| Key            | Example Value                 |
|----------------|-------------------------------|
| API_URL        | `http://javatestapi:8080`     |
| JAVATEST_API_URL | `http://<public-ip>:8080`   |

---

## 🌍 Accessing Your App

```bash
curl http://<public-ip>:8080        # javatestapi
curl http://<public-ip>:4000        # graphql-gateway
```

---

## 🔒 Step 10: Open ECS Task Ports (8080, 4000)

1. ECS → Clusters → Tasks → Task Details
2. Scroll to **Network** → Click ENI ID
3. Edit **Security Group** inbound rules:
   - Port: 8080, Source: 0.0.0.0/0 (or your IP)
   - Port: 4000, Source: 0.0.0.0/0

---

## 🏷️ Step 11: Tag AWS Resources

**🧱 ECS Services**

```bash
aws ecs tag-resource \
  --resource-arn arn:aws:ecs:us-west-2:<account-id>:service/<cluster-name>/<service-name> \
  --tags key=Project,value=javatest \
  --profile javatest-local --region us-west-2
```

**🧩 ECS Cluster**

```bash
aws ecs tag-resource \
  --resource-arn arn:aws:ecs:us-west-2:<account-id>:cluster/<cluster-name> \
  --tags key=Project,value=javatest \
  --profile javatest-local --region us-west-2
```

**🗃️ RDS Instance**
```bash
aws rds add-tags-to-resource \
  --resource-name arn:aws:rds:us-west-2:<account-id>:db:database-1 \
  --tags Key=Project,Value=javatest \
  --profile javatest-local --region us-west-2
```

## 📊 Step 12: Monitor & Scale

- **CloudWatch Logs**
- **Auto Scaling**: ECS CPU/Memory
- **Secrets Manager**
- **Task Definition Update:** New revision + Update ECS Service

---

## 🧰 Optional Enhancements

| Feature | Tool                  |
|---------|------------------------|
| CI/CD   | GitHub Actions / CodeBuild |
| Secrets | AWS Secrets Manager     |
| Metrics | CloudWatch              |
| IaC     | AWS CDK / Terraform     |
| Gateway | API Gateway (Optional)  |

---

## 📩 Java SNS/SQS Usage Example

**Publish SNS Message**

```java
SnsClient snsClient = SnsClient.create();
snsClient.publish(PublishRequest.builder()
  .topicArn("arn:aws:sns:us-west-2:123456789012:javatest-topic")
  .message("javatest.created")
  .build());
```
---

**Receive SQS Messages**

```java
SqsClient sqsClient = SqsClient.create();
sqsClient.receiveMessage(ReceiveMessageRequest.builder()
  .queueUrl("https://sqs.us-west-2.amazonaws.com/123456789012/javatest-queue")
  .maxNumberOfMessages(5)
  .waitTimeSeconds(10)
  .build())
.getMessages()
.forEach(msg -> {
    System.out.println("📬 " + msg.body());
});
```

---