# books-backend

Base de dados do projecto

![books-bd](https://user-images.githubusercontent.com/54272612/155925202-a2e2db62-cd82-49f0-9470-28426dd5dae4.PNG)

# Funcionalidade da API:
- Cadastrar, actualizar, pesquisar e eliminar usuários
- Salvar, actualizar, pesquisar e eliminar livros
- Os usuários poderão se auto-cadastrar, e a API automaticamente irá dar a permissão de pesquisar, comentar, e dar um like nos livros
- O usuário só pode actualizar ou eliminar o seu próprio comentário, ou seja, comentário feito por ele
- O usuário só pode actualizar seu próprio like, ou eliminar apenas o seu próprio like, ou seja, like dado por ele

API conta conta com 5 niveis de Roles, e cada role contém as suas permissões
- USER_AUTHORITIES (Usuário que se auto-cadastrou com permissões de pesquisar, comentar, e dar um like nos livros)
- HR_AUTHORITIES (Usuário cadastrado dentro do sistema com permissões de pesquisar e actualizar os users, livros e autores)
- MANAGER_AUTHORITIES (Usuário cadastrado dentro do sistema com permissões de pesquisar e actualizar os users, livros e autores)
- ADMIN_AUTHORITIES (Usuário cadastrado dentro do sistema com permissões de salvar, pesquisar e actualizar os users, livros e autores)
- SUPER_ADMIN_AUTHORITIES (Usuário cadastrado dentro do sistema com permissões de salvar, pesquisar, actualizar e eliminar os users, livros e autores)
