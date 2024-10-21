import {useState} from "react";
import {createBase64Signature, generateNewKeys, readKeys} from "../utils/SignatureCreator";
import {AccountDetails} from "../types/AccountDetails";
import {TransactionRequest} from "../types/TransactionRequest";

export const MainPage = () => {
    const [user, setUser] = useState('');
    const [password, setPassword] = useState('');
    const [privateKey, setPrivateKey] = useState('');
    const [publicKey, setPublicKey] = useState('');
    const [owner, setOwner] = useState<AccountDetails | null>(null);

    const [receiver, setReceiver] = useState<string>('');
    const [amount, setAmount] = useState<number>(0);

    const handleFileChange = (event: any) => {
        const file = event.target.files[0];

        if (file) {
            const reader = new FileReader();

            reader.onload = (e: any) => {
                const content = e.target.result;
                setPrivateKey(content);
            };

            reader.readAsText(file);
        }
    };

    const loadAccountData = async (privateK: string, publicK: string) => {
        try {
            const signature = createBase64Signature(user, privateKey)
            try {
                const response = await fetch(`http://localhost:8080/api/v1/accounts/${user}`, {
                    method: 'POST',
                    headers: {
                        'Accept': '*/*',
                        'signature': signature,
                    },
                    body: publicK
                })
                if (response.ok) {
                    let account: AccountDetails = await response.json()
                    console.log(account)
                    setOwner(account)
                } else {
                    console.error(response)
                }
            } catch (e) {
                console.error("Error", e)
            }
        } catch (e) {
            console.error("Could not load data", e)
        }
    }

    const doTransaction = async () => {
        let transactionRequest: TransactionRequest = {
            sender: owner!.name,
            receiver: receiver,
            amount: amount
        }
        let data = JSON.stringify(transactionRequest)
        const signature = createBase64Signature(data, privateKey)
        try {
            let response = await fetch('http://localhost:8080/api/v1/transactions', {
                method: 'POST',
                headers: {
                    'Accept': '*/*',
                    'Content-Type': 'application/json',
                    'signature': signature  // Add your signature here
                },
                body: data
            })
            if (response.ok) {
                console.log("Transaction done")
            } else {
                console.error("Exception on transaction", response)
            }
            await loadAccountData(privateKey, publicKey)
        } catch (e) {
            console.log(e)
        }
    }

    return (<div>
        {!owner && <>
            <div>Private key:</div>
            <input type={"file"} onChange={handleFileChange}/>
            <div>User:</div>
            <input
                type="text"
                value={user}
                onChange={e => setUser(e.target.value)}
            />
            <div>Password:</div>
            <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
            />
            <div>
                <button onClick={() => {
                    try {
                        let key = readKeys(user, password)
                        setPrivateKey(key.private)
                        setPublicKey(key.public)
                        console.log(key.public)
                        loadAccountData(key.private, key.public)
                    } catch (e) {
                        console.error("Could not read key", e)
                    }
                }}>Load user
                </button>
            </div>
            <div>
                <button onClick={() => generateNewKeys(user, password)}>Generate</button>
            </div>
        </>}
        {owner && <>
            <div>
                <div>Log in successfully : {owner.name}!</div>
                <div>Balance is : {owner.balance}</div>
            </div>

            <div>To:</div>
            <input
                type="text"
                value={receiver}
                onChange={e => setReceiver(e.target.value)}
            />
            <div>Amount:</div>
            <input
                type="number"
                value={amount}
                onChange={e => setAmount(Number.parseInt(e.target.value))}
            />
            <div>
                <button onClick={doTransaction}>Do transaction</button>
            </div>
        </>}
    </div>)
}
