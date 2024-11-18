# CockroachCoin


### Uruchomienie programu
Należy podać w program_arguments np. `--server.port=8086`, wówczas aplikacja wystartuje na `localhost:8086`.

### Przyłączenie węzła do sieci
Należy do endpointa `/api/v1/join_network` wysłać zapytanie `POST` z adresem *parent_node*, do którego ma się przyłączyć węzeł.
Wtedy zostanie wysłany handshake i węzeł otrzyma od *parent_node* informacje o pozostałych węzłach.


### Rozpoczęcie kopania
Należy wysłać do węzła dowolny `POST` request na `/api/v1/powerOnOff`. Tym sposobem można włączać/wyłączać kopanie.

