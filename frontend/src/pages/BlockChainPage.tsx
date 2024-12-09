import React, {useEffect, useState} from "react";
import {Block} from "../types/Block";
import {MainButton} from "../componenets/atoms/MainButton";
import {useNavigate} from "react-router-dom";

export const BlockChainPage = () => {
    const navigate = useNavigate()
    const [blocks, setBlocks] = useState<Block[]>([])

    useEffect(() => {
        fetch('http://localhost:8080/api/v1/block', {
            method: 'GET',
            headers: {
                'Accept': '*/*'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                data.reverse()
                setBlocks(data)
            })
            .catch(error => {
                console.error('Wystąpił błąd:', error);
            });

    }, []);

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
                <div>
                    <h2 className="text-2xl font-bold text-center text-gray-800">Blocks</h2>
                </div>
                <MainButton onClick={() => {
                    navigate("/wallet")
                }}>
                    Wallet
                </MainButton>
                {
                    blocks.map((elem) => (
                        <div
                            key={elem.index}
                            className="p-4 border rounded-lg bg-gray-50 shadow-sm"
                        >
                            <h3 className="text-lg font-semibold">Block Index: {elem.index}</h3>
                            <p className="text-sm text-gray-600">
                                Timestamp: {new Date(elem.timestamp).toLocaleString()}
                            </p>
                            <p
                                className="text-sm text-gray-600 truncate cursor-pointer"
                                title={elem.previousHash}
                            >
                                Previous Hash: {elem.previousHash.substring(0, 10)}...
                            </p>
                        </div>
                    ))
                }
            </div>
        </div>
    )
}
