export interface Block {
    index: number;
    transactions: Object[];
    timestamp: number;
    previousHash: string;
}
