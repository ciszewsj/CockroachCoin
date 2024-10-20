import {useState} from "react";
import {createBase64Signature} from "../utils/SignatureCreator";

export const MainPage = () => {
    const [user, setUser] = useState('');
    const [fileContent, setFileContent] = useState('');

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

    const loadAccountData = () => {
        const signature = createBase64Signature(user, fileContent)
        console.log(signature)
        // fetch(`http://localhost:8080/api/v1/accounts/${user}`)
    }

    return (<div>
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
        <div>To:</div>
        <input/>
        <div>Amount:</div>
        <input/>
        <div>
            <button>Do transaction</button>
        </div>
    </div>)
}
