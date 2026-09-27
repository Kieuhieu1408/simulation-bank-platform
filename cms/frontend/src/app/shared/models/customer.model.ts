/**
 * Customer information returned by the backend.
 * Sensitive fields (phone, cccd) are masked server-side before delivery.
 */
export interface CustomerResponse {
  /** Core Banking CIF (Customer Information File) number */
  cif: string;
  /** Full display name */
  fullName: string;
  /** Masked phone number, e.g. "090****789" */
  phone: string;
  /** Masked CCCD (national ID), e.g. "0123****5678" */
  cccd: string;
}
