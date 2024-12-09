export interface TransactionRequest {
    senders: Sender[],
    receivers: Receivers[],
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
