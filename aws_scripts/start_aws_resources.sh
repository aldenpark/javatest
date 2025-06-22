#!/usr/bin/env bash
set -e

PROFILE="javatest-local"
REGION="us-west-2"

echo "Starting EC2 instances (tag: Project=javatest)"
EC2_IDS=$(aws ec2 describe-instances \
  --filters "Name=tag:Project,Values=javatest" "Name=instance-state-name,Values=stopped" \
  --query "Reservations[].Instances[].InstanceId" --output text --profile "$PROFILE" --region "$REGION")

if [ -n "$EC2_IDS" ]; then
  aws ec2 start-instances --instance-ids $EC2_IDS --profile "$PROFILE" --region "$REGION"
  echo "🚀 EC2 start initiated: $EC2_IDS"
else
  echo "No stopped EC2 instances found"
fi

echo "Starting AWS Fargate/ECS services with tag Project=javatest"
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
      --desired-count 1 \
      --profile "$PROFILE" --region "$REGION" \
      > /dev/null
    echo "Started service: $SERVICE_NAME"
  fi
done

echo "Starting RDS instance 'database-1'"
aws rds start-db-instance --db-instance-identifier database-1 \
  --profile "$PROFILE" --region "$REGION" > /dev/null || \
  echo "RDS instance already running or cannot be started"

echo "All resources have been started."
