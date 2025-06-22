#!/usr/bin/env bash
set -e

PROFILE="javatest-local"
REGION="us-west-2"

echo "Stopping EC2 instances (tag: Project=javatest)"
EC2_IDS=$(aws ec2 describe-instances \
  --filters "Name=tag:Project,Values=javatest" "Name=instance-state-name,Values=running" \
  --query "Reservations[].Instances[].InstanceId" --output text --profile "$PROFILE" --region "$REGION")

if [ -n "$EC2_IDS" ]; then
  aws ec2 stop-instances --instance-ids $EC2_IDS --profile "$PROFILE" --region "$REGION"
  echo "⏳ EC2 stop initiated: $EC2_IDS"
else
  echo "No running EC2 instances found"
fi

echo "Stopping AWS Fargate/ECS services with tag Project=javatest"
SERVICE_ARNS=$(aws ecs list-services --cluster javatest-cluster \
  --profile "$PROFILE" --region "$REGION" \
  --query "serviceArns[]" --output text)

for ARN in $SERVICE_ARNS; do
  TAG_VALUE=$(aws ecs list-tags-for-resource \
    --resource-arn "$ARN" --profile "$PROFILE" --region "$REGION" \
    --query "tags[?key=='Project'].value" --output text)
  if [ "$TAG_VALUE" == "javatest" ]; then
    SERVICE_NAME=$(basename "$ARN")
    aws ecs update-service \
      --cluster javatest-cluster \
      --service "$SERVICE_NAME" \
      --desired-count 0 \
      --profile "$PROFILE" --region "$REGION" \
      > /dev/null
    echo "Stopped service: $SERVICE_NAME"
  fi
done

echo "Suspending RDS instance 'database-1'"
aws rds stop-db-instance --db-instance-identifier database-1 \
  --profile "$PROFILE" --region "$REGION" > /dev/null || \
  echo "RDS instance already stopped or cannot be stopped"

echo "All billing-capable resources for tag: Project=javatest have been stopped."

echo -e "\nChecking all AWS resources...\n"

echo -e "\nEC2 Instances"
aws ec2 describe-instances --query "Reservations[*].Instances[*].[InstanceId,State.Name,Tags]" --output table

echo -e "\nECS/Fargate Services"
aws ecs list-clusters --query "clusterArns[]" --output text | tr '\t' '\n' | while read cluster; do
  echo "Checking cluster: $cluster"
  aws ecs list-services --cluster "$cluster" --query "serviceArns[]" --output text | tr '\t' '\n' | while read service; do
    aws ecs describe-services --cluster "$cluster" --services "$service" \
      --query "services[?runningCount!=\`0\`].[serviceName,runningCount]" --output table
  done
done

echo -e "\nEKS Clusters"
aws eks list-clusters --output table

echo -e "\nRDS Instances"
aws rds describe-db-instances --query "DBInstances[*].[DBInstanceIdentifier,DBInstanceStatus,Engine]" --output table

echo -e "\nMSK (Kafka) Clusters"
aws kafka list-clusters --query "ClusterInfoList[*].[ClusterName,State]" --output table

echo -e "\nECR Repositories (Optional for cleanup)"
aws ecr describe-repositories --query "repositories[*].repositoryName" --output table

echo -e "\nCloudWatch Log Groups (Optional, can accumulate cost)"
aws logs describe-log-groups --query "logGroups[*].[logGroupName, storedBytes]" --output table

echo -e "\n"
# Cleanup Tip: when done with the stack use `cdk destroy``
  