# CockroachCoin


### Uruchomienie programu
Należy podać w program_arguments np. `--server.port=8086`, wówczas aplikacja wystartuje na `localhost:8086`.
Konieczne jest również podanie jako argumentu wywołania adresu, do którego węzeł ma się później połączyć.

Przykładowo:
```
--server.port=8086 --config.connectToUrl=http://localhost:8080
```

Dodatkowo, węzeł inicjalizujący musi mieć też argument `--config.isInit=true`.

### Przyłączenie węzła do sieci
Należy do endpointa `/api/v1/join_network` wysłać zapytanie `POST`.
Wtedy zostanie wysłany handshake i węzeł otrzyma od *parent_node* informacje o pozostałych węzłach.


### Rozpoczęcie kopania
Należy wysłać do węzła dowolny `POST` request na `/api/v1/powerOnOff`. Tym sposobem można włączać/wyłączać kopanie.

