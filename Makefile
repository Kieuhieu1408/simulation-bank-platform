.PHONY: infra k8s build down deploy-k8s clean-k8s test-k8s

infra:
	@echo "🚀 Khởi động Hạ tầng (DB, Redis, Kafka, Observability)..."
	docker compose up -d

build:
	@echo "📦 Build các Docker images cho Microservices..."
	docker compose --profile apps build

k8s:
	@echo "🚢 Triển khai Microservices lên Kubernetes..."
	helmfile apply

deploy-k8s:
	@bash scripts/deploy-k8s.sh

clean-k8s:
	@bash scripts/cleanup-k8s.sh

down:
	@echo "🧹 Dọn dẹp toàn bộ hệ thống..."
	docker compose down
	helmfile destroy

