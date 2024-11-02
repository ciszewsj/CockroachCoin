import React, {FC, MouseEventHandler} from 'react';

export const SecondaryButton: FC<{
    children?: string | undefined;
    onClick?: MouseEventHandler<any> | undefined;
}> = ({onClick, children}) => {
    return (
        <button
            type="button"
            className="w-full px-4 py-2 text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300 focus:ring-4 focus:ring-gray-400"
            onClick={onClick}
        >
            {children}
        </button>
    );
};
