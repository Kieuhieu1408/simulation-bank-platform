export type ProposalType =
  | 'CHANGE_PWD'
  | 'CHANGE_PHONE'
  | 'CHANGE_CCCD'
  | 'CHANGE_CIF';

export type ProposalStatus =
  | 'PENDING'
  | 'APPROVED'
  | 'REJECTED'
  | 'CANCELLED';

export interface Proposal {
  id: string;
  proposalCode: string;
  customerCif: string;
  proposalType: ProposalType;
  status: ProposalStatus;
  /** Employee ID of the maker (creator) */
  makerId: string;
  /** Employee ID of the checker (approver), null when not yet reviewed */
  checkerId: string | null;
  /** ISO-8601 timestamp */
  createdAt: string;
}
