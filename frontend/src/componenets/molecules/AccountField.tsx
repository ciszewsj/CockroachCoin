import {FC, useContext, useEffect, useState} from "react";
import {AccountDetails} from "../../types/AccountDetails";
import {HttpAddressContext} from "../../context/HttpAddressProvider";

export const AccountField: FC<{
    accounts: AccountDetails
    handleDelete: any
}> = ({accounts, handleDelete}) => {
    const [address] = useContext(HttpAddressContext)!!;

    const [balance, setBalance] = useState("NOT_READY")


    useEffect(() => {
        setBalance("NOT_READY")
        fetch(address + '/api/v1/accounts', {
            method: 'POST',
            headers: {
                'accept': '*/*',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify("string")
        })
            .then(response => response.json())
            .then(data => setBalance(data.balance))
            .catch(_ => setBalance('Error'));

    }, [address]);

    const cleanKey = (key: string) => {
        return key.replace(/-----BEGIN PUBLIC KEY-----/g, '')
            .replace(/-----END PUBLIC KEY-----/g, '')
            .replace(/\s+/g, '')
    }

    const exportKey = () => {
        const content = accounts.privateKey;
        const blob = new Blob([content], {type: 'text/plain'});
        const url = URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.href = url;
        link.download = generateUUID() + '.txt';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    }


    return (
        <div className="p-3 bg-gray-100 rounded-lg flex items-center justify-between">
            <div className="max-w-xs overflow-hidden">
                <p className="text-gray-700 text-md font-bold">Amount: {balance}</p>
                <p className="text-gray-700 text-sm truncate"
                   title={accounts.publicKey}>
                    {cleanKey(accounts.publicKey)}
                </p>
            </div>

            <div className="flex flex-col items-end space-y-2">
                <button
                    className="px-3 py-1 text-sm text-white bg-green-600 rounded hover:bg-green-700 focus:ring-2 focus:ring-green-300"
                    onClick={exportKey}>
                    Export
                </button>
                <button
                    className="px-3 py-1 text-sm text-white bg-red-600 rounded hover:bg-red-700 focus:ring-2 focus:ring-red-300"
                    onClick={() => {
                        handleDelete(accounts.privateKey)
                    }}>
                    Delete
                </button>
            </div>
        </div>

    )
}

const generateUUID = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
        const r = Math.random() * 16 | 0;
        // eslint-disable-next-line no-mixed-operators
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
};
