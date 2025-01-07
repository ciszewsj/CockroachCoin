export interface TransactionRequest {
    timestamp: number,
    senders: Sender[],
    receivers: Receivers[],
}

export interface SigningObject {
    index: number,
    timestamp: number,
    amount: number,
    out: Receivers[]
}

export interface Sender {
    senderKey: string,
    amount: number,
    signature: string
}

export interface Receivers {
    senderKey: string,
    amount: number
}
