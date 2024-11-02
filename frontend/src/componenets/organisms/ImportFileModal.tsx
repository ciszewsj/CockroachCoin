import React, {FC, useEffect, useState} from 'react';
import {privateKeyToPublic} from "../../utils/SignatureCreator";
import {KeysObject} from "../../types/KeysObject";

const ImportFileModal: FC<{
    open: boolean,
    setOpen: any,
    setKeys: any,
    keys: KeysObject[]
}> = ({open, setOpen, setKeys, keys}) => {
    const [file, setFile] = useState(null);

    useEffect(() => {
        setFile(null)
    }, [open]);

    const handleFileChange = (e: any) => {
        setFile(e.target.files[0]);
    };

    const handleImport = () => {
        if (file) {
            const reader = new FileReader();

            reader.onload = (event) => {
                try {
                    const fileContent = event?.target?.result;
                    if (fileContent) {
                        const publicKey = privateKeyToPublic(fileContent.toString());
                        setKeys(
                            [
                                ...keys,
                                {
                                    privateKey: fileContent,
                                    publicKey: publicKey,
                                }
                            ]
                        )
                        setOpen(false)
                        return
                    }
                    console.error("COULD NOT READ PRIVATE KEY")
                } catch (e: any) {
                    console.error("COULD NOT READ PRIVATE KEY")
                }
            };

            reader.onerror = (error) => {
                console.error("Error reading file:", error);
            };

            reader.readAsText(file);
        }
    };


    if (!open) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-6 max-w-sm w-full shadow-lg">
                <h2 className="text-xl font-semibold mb-4 text-center">Import private key</h2>
                <input type="file" onChange={handleFileChange} className="mb-4 block w-full text-sm text-gray-700"/>
                <div className="flex justify-end space-x-3">
                    <button onClick={handleImport}
                            className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 focus:ring-2 focus:ring-green-300">
                        Import
                    </button>
                    <button
                        className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 focus:ring-2 focus:ring-red-300"
                        onClick={() => setOpen(false)}>
                        Close
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ImportFileModal;
