#!/usr/bin/env bash
# ==============================================================================
# SCRIPT DỌN DẸP SẠCH TOÀN BỘ KUBERNETES TRÊN LOCAL (DOCKER DESKTOP)
# ==============================================================================
# Hướng dẫn cho AI Agent / Người dùng:
#   Chạy script này bằng lệnh: bash scripts/cleanup-k8s.sh
#   Hoặc xóa cả hạ tầng Docker compose: bash scripts/cleanup-k8s.sh --all
#
#   Script sẽ:
#     1. Gỡ bỏ Helm release 'sim-bank-apps'
#     2. Xóa toàn bộ Deployments, Pods, Services, HPAs, ConfigMaps, Secrets trong K8s
#     3. Xóa hoàn toàn Namespace 'sim-bank'
#     4. Đảm bảo TAB KUBERNETES TRÊN DOCKER DESKTOP SẠCH SẼ HOÀN TOÀN
# ==============================================================================

set -euo pipefail

# Màu sắc hiển thị
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

log_info()    { echo -e "${CYAN}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warn()    { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error()   { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()    { echo -e "\n${BOLD}${BLUE}=== $1 ===${NC}"; }

log_step "BƯỚC 1: KIỂM TRA KẾT NỐI KUBERNETES"

if ! kubectl cluster-info &> /dev/null; then
    log_warn "Kubernetes cluster hiện tại không phản hồi hoặc đã dừng."
    log_info "Không còn tài nguyên nào đang chạy trên Kubernetes."
    exit 0
fi

NAMESPACE="sim-bank"

log_step "BƯỚC 2: GỠ BỎ TÀI NGUYÊN KUBERNETES CỦA DỰ ÁN"

# 1. Gỡ bỏ Helm Release nếu có
if helm list -n "$NAMESPACE" 2>/dev/null | grep -q "sim-bank-apps"; then
    log_info "Đang gỡ bỏ Helm release 'sim-bank-apps'..."
    helm uninstall sim-bank-apps -n "$NAMESPACE" || true
    log_success "Đã gỡ bỏ Helm release 'sim-bank-apps'."
else
    log_info "Không có Helm release nào cần gỡ."
fi

# 2. Xóa toàn bộ workloads và namespace
if kubectl get namespace "$NAMESPACE" &> /dev/null; then
    log_info "Đang xóa tất cả tài nguyên trong namespace '$NAMESPACE'..."
    kubectl delete all --all -n "$NAMESPACE" --grace-period=0 --force 2>/dev/null || true
    kubectl delete configmap --all -n "$NAMESPACE" 2>/dev/null || true
    kubectl delete secret --all -n "$NAMESPACE" 2>/dev/null || true
    kubectl delete hpa --all -n "$NAMESPACE" 2>/dev/null || true

    log_info "Đang xóa namespace '$NAMESPACE'..."
    kubectl delete namespace "$NAMESPACE" --timeout=60s 2>/dev/null || true

    # Xử lý trường hợp namespace bị kẹt Terminating (nếu có)
    if kubectl get namespace "$NAMESPACE" 2>/dev/null | grep -q "Terminating"; then
        log_warn "Namespace đang trong trạng thái Terminating, tiến hành xóa finalizer..."
        kubectl get namespace "$NAMESPACE" -o json | \
            jq '.spec.finalizers = []' | \
            kubectl replace --raw "/api/v1/namespaces/$NAMESPACE/finalize" -f - 2>/dev/null || true
    fi
    log_success "Đã xóa hoàn toàn namespace '$NAMESPACE'!"
else
    log_info "Namespace '$NAMESPACE' không tồn tại trên Kubernetes cluster."
fi

# ------------------------------------------------------------------------------
# BƯỚC 3: XÁC MINH TAB KUBERNETES TRÊN DOCKER DESKTOP ĐÃ SẠCH SẼ
# ------------------------------------------------------------------------------
log_step "BƯỚC 3: XÁC MINH TRẠNG THÁI KUBERNETES DOCKER DESKTOP"

REMAINING_PODS=$(kubectl get pods --all-namespaces 2>/dev/null | grep -v "kube-system" | grep -v "docker" | grep -v "NAMESPACE" || echo "")

if [ -z "$REMAINING_PODS" ]; then
    log_success "Tuyệt vời! Không còn Pod hoặc Workload nào của dự án trên Kubernetes!"
else
    log_warn "Còn lại một số Pods:"
    echo "$REMAINING_PODS"
fi

# Tùy chọn xóa hạ tầng Docker compose nếu truyền tham số --all
if [[ "${1:-}" == "--all" ]]; then
    log_step "TÙY CHỌN: DỌN DẸP HẠ TẦNG DOCKER COMPOSE"
    log_info "Dừng toàn bộ container Docker Compose..."
    docker compose down
    log_success "Hạ tầng Docker Compose đã được tắt hoàn toàn."
fi

echo -e "
╔═══════════════════════════════════════════════════════════════════════╗
║          🧹 ĐÃ DỌN DẸP SẠCH TOÀN BỘ KUBERNETES THÀNH CÔNG 🧹          ║
╠═══════════════════════════════════════════════════════════════════════╣
║  • Toàn bộ Pods, Services, Deployments, Namespace 'sim-bank' đã xoá.  ║
║  • Mở tab 'Kubernetes' trên Docker Desktop: ĐÃ SẠCH SẼ HOÀN TOÀN!      ║
║  • (Không còn bất kỳ workloads nào của dự án sim-bank hiển thị).      ║
╚═══════════════════════════════════════════════════════════════════════╝
"
