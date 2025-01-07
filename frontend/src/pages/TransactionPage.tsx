import {SecuredPage} from "./SecuredPage";
import React, {FC, useContext, useState} from "react";
import {CredentialsDto} from "../types/CredentialsDto";
import {KeysObject} from "../types/KeysObject";
import {MainButton} from "../componenets/atoms/MainButton";
import {useNavigate} from "react-router-dom";
import {SecondaryButton} from "../componenets/atoms/SecondaryButton";
import {ReceiversTransactionCreator} from "../componenets/organisms/ReceiversTransactionCreator";
import {SendersTransactionCreator} from "../componenets/organisms/SendersTransactionCreator";
import {SenderCreatorDto} from "../types/SenderCreatorDto";
import {ReceiverCreatorDto} from "../types/ReceiverCreatorDto";
import {HttpAddressContext} from "../context/HttpAddressProvider";
import {cleanKey} from "../utils/ClearKey";
import {SigningObject, TransactionRequest} from "../types/TransactionRequest";
import {createBase64Signature} from "../utils/SignatureCreator";

export const TransactionPage: FC<{
    credentials?: CredentialsDto,
    keys: KeysObject[],
    setKeys: React.Dispatch<React.SetStateAction<KeysObject[]>>,
}> = ({credentials, keys, setKeys}) => {
    const navigate = useNavigate()
    const [address] = useContext(HttpAddressContext)!!;

    const [senders, setSenders] = useState<SenderCreatorDto[]>([]);
    const [receivers, setReceivers] = useState<ReceiverCreatorDto[]>([])


    return (
        <SecuredPage credentials={credentials}>
            <div className="flex items-center justify-center min-h-screen bg-gray-100">
                <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
                    <h2 className="text-2xl font-bold text-center text-gray-800">Do transaction</h2>
                    <SecondaryButton onClick={() => {
                        navigate("/wallet")
                    }}>
                        Wallet
                    </SecondaryButton>
                    <SendersTransactionCreator keys={keys} setSenders={setSenders} senders={senders}/>
                    <ReceiversTransactionCreator receivers={receivers} setReceivers={setReceivers}/>
                    <MainButton onClick={() => {
                        const time = Date.now()

                        const recivers = receivers.map(receiver => {
                            return {
                                senderKey: receiver.key,
                                amount: receiver.amount,
                            }
                        })
                        let index = 0
                        const sendersR = senders.map(sender => {
                            const signingObject: SigningObject = {
                                index: index++,
                                timestamp: time,
                                amount: sender.amount,
                                out: recivers,
                            }
                            const objToSing = JSON.stringify(signingObject)
                            console.error(objToSing)
                            return {
                                senderKey: cleanKey(sender.key.publicKey),
                                amount: sender.amount,
                                signature: createBase64Signature(objToSing, sender.key.privateKey)
                            }
                        })

                        const transactionRequest: TransactionRequest = {
                            timestamp: time,
                            senders: sendersR,
                            receivers: recivers

                        }

                        fetch(address + '/api/v1/transactions', {
                            method: 'POST',
                            headers: {
                                'accept': '*/*',
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify(transactionRequest)
                        })
                            .then(response => {
                                alert("Status: " + response.status)
                            })
                            .catch(_ => alert('Failed'));
                    }}>
                        Do tranasaction
                    </MainButton>
                </div>
            </div>
        </SecuredPage>
    )
}
