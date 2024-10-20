import {useState} from "react";
import {createBase64Signature} from "../utils/SignatureCreator";
import {AccountDetails} from "../types/AccountDetails";
import {TransactionRequest} from "../types/TransactionRequest";

export const MainPage = () => {
    const [user, setUser] = useState('');
    const [fileContent, setFileContent] = useState('');
    const [owner, setOwner] = useState<AccountDetails | null>(null);

    const [receiver, setReceiver] = useState<string>('');
    const [amount, setAmount] = useState<number>(0);

    const handleFileChange = (event: any) => {
        const file = event.target.files[0];

        if (file) {
            const reader = new FileReader();

            reader.onload = (e: any) => {
                const content = e.target.result;
                setFileContent(content);
            };

            reader.readAsText(file);
        }
    };

    const loadAccountData = async () => {
        const signature = createBase64Signature(user, fileContent)
        try {
            const response = await fetch(`http://localhost:8080/api/v1/accounts/${user}`, {
                method: 'GET',
                headers: {
                    'Accept': '*/*',
                    'signature': signature
                }
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
    }

    const doTransaction = async () => {
        let transactionRequest: TransactionRequest = {
            sender: owner!.name,
            receiver: receiver,
            amount: amount
        }
        let data = JSON.stringify(transactionRequest)
        const signature = createBase64Signature(data, fileContent)
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
            await loadAccountData()
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
            <div>
                <button onClick={loadAccountData}>Load user</button>
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
