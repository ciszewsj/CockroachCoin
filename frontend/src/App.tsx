import React, {useState} from 'react';
import {LoginPage} from "./pages/LoginPage";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {WalletPage} from "./pages/WalletPage";
import {CredentialsDto} from "./types/CredentialsDto";
import {KeysObject} from "./types/KeysObject";

function App() {

    const [credentials, setCredentials] = useState<CredentialsDto>()
    const [keys, setKeys] = useState<KeysObject[]>([])

    return (
        <BrowserRouter>
            <Routes>
                <Route path={"/"} element={<LoginPage setCredentials={setCredentials} setKeys={setKeys}/>}/>
                <Route path={"/wallet"}
                       element={<WalletPage credentials={credentials} setKeys={setKeys} keys={keys}/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
