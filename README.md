# Sistemas Distribuídos - TP1
Trabalho 01 de Sistemas Distribuídos - LEI 2021/2022

##### Instruções para compilar e executar #####

Desenvolvemos este projeto no IDE Intellij pelo que recomendamos que, para o compilar e executar, importem a pasta "ProjectIntelli" fornecida exatamente como está para esse IDE. Assim podem compilar e executar à vontade. 
Têm apenas de ter atenção à diretoria onde essa pasta se encontra localmente e alterar os endereços correspondentes às Homes nos ficheiros de configuração dos servidores (explicado mais à frente).


##### Configuração e ficheiros estáticos #####
  
Este trabalho contém cinco ficheiros usados para conter as informações dos servidores e clientes. Os ficheiros que são usados nos servidores estão duplicados, isto porque os servidores poderão estar em locais diferentes e cada um necessita de ter o seu ficheiro.

- clients1.txt: Contém as informações de cada cliente presente nos sistema, cada informação está separada por "/" e cada cliente corresponde a uma linha. Este ficheiro irá corresponder aos clientes para o Servidor1

- clients2.txt: Contém as informações de cada cliente presente nos sistema, cada informação está separada por "/" e cada cliente corresponde a uma linha. Este ficheiro irá corresponder aos clientes para o Servidor2

Nota: Os ficheiros acima devem ter as mesmas informações.

- config_server1.txt: Contém as informações de cada servidor, cada informação corresponde a uma linha do ficheiro. No final temos ainda número máximo de pings perdidos e o tempo entre cada ping em milisegundos.

- config_server2.txt: Contém as informações de cada servidor, cada informação corresponde a uma linha do ficheiro. No final temos ainda número máximo de pings perdidos e o tempo entre cada ping em milisegundos.

Nota: Os ficheiros acima devem ter as mesmas informações.

- config_client.txt: Contêm os portos e endereços com que o cliente se vai tentar ligar aos servidores, 1 e 2, respetivamente, via TCP.

##### Propriedades do servidor no ficheiro config_server*.txt #####
Nota: Valores usados por default

- Endereço do servidor primário: 127.0.0.1
- Porto TCP com que o servidor comunica com os clientes: 6000
- Porto UDP com que o servidor comunica com o outro servidor: 6789
- Porto UDP para envio de ficheiros entre servidores: 6900
- Diretoria atual do servidor1: C:\Users\Utilizador\Universidade\3_Ano\2_Semestre\SD\ProjectIntelli\src\server\Server_Homes
- Endereço do servidor secundário: 127.0.0.8
- Porto TCP com que o servidor comunica com os clientes: 5000
- Porto UDP com que o servidor comunica com o outro servidor: 6789
- Porto UDP para envio de ficheiros entre servidores: 6900
- Diretoria atual do servidor2: C:\Users\Utilizador\Universidade\3_Ano\2_Semestre\SD\ProjectIntelli\src\server2\Server_Homes
- Número máximo de pings perdidos: 5
- Tempo entre cada ping em milisegundos: 1000

##### Propriedades do cliente no ficheiro config_client.txt #####
Nota: Valores usados por default

- Porto TCP com que o servidor comunica com os clientes: 6000
- Porto TCP com que o servidor comunica com os clientes: 5000
- Endereço do servidor primário: 127.0.0.1
- Endereço do servidor secundário: 127.0.0.8

##### Propriedades do cliente no ficheiro client*.txt #####
Nota 1: Cada informação de cada cliente está separada por "/" numa única linha
Nota 2: Se for o primeiro acesso, não deve existir o último parâmetro, não havendo "/" a seguir à validade do cartão de cidadão

- Username
- Password
- Departamento/Faculdade a que pertence
- Contacto telefónico
- Morada
- Validade do cartão de cidadão
- Diferença de strings entre a diretoria atual dos servidores e o último endereço acedido pelo cliente no servidor 

##### Autores #####
  - Duarte Meneses - 2019216949
  - Patrícia Costa - 2019213995

