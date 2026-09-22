---
title: "Cloud Service Provider Security Mandates (2024)"
status: "revoked"
effective_to: "2025-12-31"
document_id: "OLD-112"
---
# Cloud Service Provider Security Mandates (2024)

<a id="OLD-112-D01"></a>
## 1. Data Localization
All Tier-1 transactional data, including Paygate cryptographic keys and Ngân hàng Mô phỏng core banking DB backups, must reside on bare-metal servers physically located within the bank's on-premises data centers. Public cloud infrastructure (AWS, Azure, GCP) is strictly forbidden for anything above Tier-3 (Public Website/Marketing).

<a id="OLD-112-D02"></a>
## 2. Encryption Standards
Data at rest within the on-premises perimeter may utilize AES-128 encryption. Data in transit internally between microservices does not require TLS encryption if traversing the dedicated local area network (LAN).
