import {FC} from "react";
import {AccountDetails} from "../../types/AccountDetails";

export const AccountField: FC<{
    accounts: AccountDetails
}> = ({accounts}) => {

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
                <p className="text-gray-700 text-md font-bold">Amount: {accounts.balance}</p>
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
                    className="px-3 py-1 text-sm text-white bg-red-600 rounded hover:bg-red-700 focus:ring-2 focus:ring-red-300">
                    Delete
                </button>
            </div>
        </div>

    )
}

const generateUUID = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
};
