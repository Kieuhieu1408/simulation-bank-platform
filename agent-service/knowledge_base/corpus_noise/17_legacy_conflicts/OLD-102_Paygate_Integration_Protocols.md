---
title: "Paygate Integration Protocols v1.4 (2024)"
status: "revoked"
effective_to: "2025-12-31"
document_id: "OLD-102"
---
# Paygate Integration Protocols v1.4 (2024)

<a id="OLD-102-D01"></a>
## 1. Supported Authentication Methods
All internal services of Ngân hàng Mô phỏng connecting to Paygate core APIs must utilize Basic Authentication (Base64 encoded client_id:client_secret) over TLS 1.1 or higher. OAuth2.0 is supported but entirely optional for internal microservices originating from the DMZ subnet.

<a id="OLD-102-D02"></a>
## 2. Webhook Delivery and Retries
Paygate will dispatch transaction status webhooks to registered merchant endpoints. In the event of a failure (HTTP 5xx), the system will retry precisely 3 times with a static 30-second delay between attempts. No exponential backoff is implemented in this version.
