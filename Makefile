.PHONY: infra k8s build down

infra:
	@echo "🚀 Khởi động Hạ tầng (DB, Redis, Kafka, Observability)..."
	docker compose up -d

build:
	@echo "📦 Build các Docker images cho Microservices..."
	docker compose --profile apps build

k8s:
	@echo "🚢 Triển khai Microservices lên Kubernetes..."
	helmfile apply

down:
	@echo "🧹 Dọn dẹp toàn bộ hệ thống..."
	docker compose down
	helmfile destroy
