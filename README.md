# SGCF - Back-end

Back-end do SGCF, sistema de gestão de clientes, funcionários, tours, reservas e metas de uma agência de turismo.
Feito em Java 17 com Spring Boot, PostgreSQL e autenticação com JWT.

O front-end (Angular) fica em outro repositório: https://github.com/Thurlex/Web-SGCF

## Como rodar local

Precisa ter Java 17, Maven e PostgreSQL instalados.

1. Criar o banco no Postgres (ex: `sgcf_db`)
2. Configurar as variáveis de ambiente (ou editar o `application.properties` só na sua máquina, sem commitar):

```
DB_URL=jdbc:postgresql://localhost:5432/sgcf_db
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
API_SECURITY_TOKEN_SECRET=qualquer_texto_grande_aqui
```

3. Rodar:

```
mvn spring-boot:run
```

A API sobe na porta 8080.

Para rodar os testes: `mvn test`

## Deploy na AWS (EC2)

O deploy é feito pelo script `setup.sh`, que está na raiz desse repositório. Ele instala tudo, baixa o código do back e do front, faz o build e deixa a aplicação rodando.

### 1. Criar a instância

- Ubuntu 22.04 ou 24.04
- t2.micro ou t3.micro serve (o script cria um swap de 2GB pra não travar no build)
- No Security Group, liberar só essas portas:
  - 22 (SSH) - de preferência só pro seu IP
  - 80 (HTTP) - para qualquer lugar
  - 443 (HTTPS) - opcional

A porta 5432 do PostgreSQL **não** deve ser liberada. O banco só é acessado pela própria máquina.

### 2. Rodar o script

Entrar na instância por SSH e rodar:

```
git clone https://github.com/Camilaronzzani/SGCF_back.git
cd SGCF_back
chmod +x setup.sh
./setup.sh
```

No começo ele pede a senha que vai ser usada no banco e a senha de app do Gmail (para o "esqueci minha senha"). Se não tiver a do Gmail, é só dar enter, o resto funciona normal.

Demora uns 10 a 15 minutos. No final ele mostra o endereço pra acessar, tipo `http://IP_DA_INSTANCIA`.

### O que o script faz

1. Atualiza o sistema e instala PostgreSQL, Java 17, Maven, Node 20 e Apache
2. Cria o usuário e o banco `sgcf_db`
3. Clona os repositórios do back e do front em `/opt/sgcf`
4. Gera o `.jar` do back
5. Cria o arquivo `/etc/sgcf/sgcf.env` com as senhas e o secret do JWT (gerado na hora, não fica no código)
6. Cria o serviço `sgcf` no systemd, pra o back rodar em segundo plano e subir sozinho se a máquina reiniciar
7. Faz o build de produção do Angular e copia para `/var/www/sgcf`
8. Configura o Apache para:
   - servir o front
   - mandar tudo que começa com `/api` para o back na porta 8080
   - redirecionar as rotas do Angular para o `index.html` (senão dá 404 quando dá F5 em `/clientes`, por exemplo)

Se precisar atualizar a aplicação depois de um commit novo, é só rodar o `./setup.sh` de novo.

## Ver os logs na EC2

Back-end:

```
sudo journalctl -u sgcf -f
```

Status do serviço:

```
sudo systemctl status sgcf
```

Reiniciar o back:

```
sudo systemctl restart sgcf
```

Apache:

```
sudo tail -f /var/log/apache2/sgcf_error.log
sudo tail -f /var/log/apache2/sgcf_access.log
```

## Segurança

- Login gera um token JWT que expira em 30 minutos
- Todas as rotas precisam do token, menos login e "esqueci minha senha"
- Cadastro de usuário e tela de logs são só para MANAGER (`@PreAuthorize`)
- Sem token ou com token inválido a API retorna 403

## Equipe

- Frantony Nieves
- Alex Dias
- Camila Ronzzani
- Kauan Pereira
