import {FC} from "react";
import {CredentialsDto} from "../types/CredentialsDto";
import {Navigate} from "react-router-dom";

export const SecuredPage: FC<{
    credentials?: CredentialsDto,
    children?: any
}> = ({credentials, children}) => {
    return (
        <>
            {credentials ?
                <>{children}</> :
                <Navigate to={"/"}/>
            }
        </>
    )
}


