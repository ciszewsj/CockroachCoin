import React, {useState} from 'react';
import {LoginPage} from "./pages/LoginPage";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {WalletPage} from "./pages/WalletPage";
import {CredentialsDto} from "./types/CredentialsDto";
import {SecuredPage} from "./pages/SecuredPage";

function App() {

    const [credentials, setCredentials] = useState<CredentialsDto>()

    console.log(credentials)

    return (
        <BrowserRouter>
            <Routes>
                <Route path={"/"} element={<LoginPage setCredentials={setCredentials}/>}/>
                <Route path={"/wallet"} element={
                    <SecuredPage credentials={credentials}>
                        <WalletPage/>
                    </SecuredPage>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
