# checkpoint1
GLOBAL SOLUTIONS - MODELO 2
Fique atento ao horário de upload da prova (após a finalização não haverá como enviar o
projeto)
Salve o projeto no D:\, pois, caso haja algum problema com a máquina existe a
possibilidade de recuperá-lo.
IMPORTANTE!
● Caso o build do projeto não funcione a nota será 0.
● Caso seja identificado qualquer indício de plágio, cópia parcial ou total de outra
fonte (incluindo colegas, internet, IA ou materiais anteriores), a nota atribuída
será automaticamente 0 (zero), sem direito a recurso.
CONTEXTO
No cenário atual, muitas empresas adotam o modelo híbrido de trabalho, combinando
colaboradores no escritório e em home office. Embora ofereça flexibilidade, este formato
traz desafios de engajamento, comunicação e motivação entre equipes distribuídas.
Para apoiar esses desafios, uma abordagem cada vez mais adotada é a gamificação —
isto é, a aplicação de elementos típicos de jogos (como pontuação, desafios, conquistas,
rankings e missões) em atividades profissionais, tornando tarefas mais dinâmicas e
motivadoras.
Nesta prova, você desenvolverá um aplicativo em Flutter que utiliza a gamificação em um
ambiente corporativo híbrido. O objetivo é permitir que colaboradores possam participar
de projetos, acumular pontos e se engajar com desafios de forma dinâmica.
TAREFAS
Desenvolver um aplicativo Flutter capaz de consumir uma API REST, armazenar dados
localmente (SharedPreferences) e permitir interação entre usuários.
Entrega:
O aluno deve entregar:
- O arquivo main.dart contendo todo o código solicitado.
- O aplicativo deve rodar sem erros.
API para consumo:
Documentação: https://vida-equilibrio-api-69a356625afd.herokuapp.com/docs/
BaseURL: https://vida-equilibrio-api-69a356625afd.herokuapp.com
Requisitos do Aplicativo:
1. (2,0 pontos) O app deve permitir que o usuário informe o userId.
- O userId deve ser salvo no SharedPreferences.
- Caso o userId ainda não esteja salvo, o app deve abrir automaticamente uma tela
solicitando esse dado.
- Na AppBar deve existir um ícone para alterar o userId.
2. (4,0 pontos) Tela “Todas atividades” deve:
- Consumir a rota GET /atividades.
- Exibir a lista das atividades retornadas com os campos retornados da api.
- Ao clicar em uma atividade abrir a tela de Detalhe da Atividade.
- Exibir Snackbar para feedback de sucesso/erro.
3. (4,0 pontos) Tela “Detalhe da Atividade” deve:
- Consumir a rota GET /atividades/{id} com o nome do usuário como queryParam
- Exibir os detalhes da atividade
- Caso o usuário já participe da tarefa mostrar um botão para Sair.
- Caso o usuário não participe da tarefa mostrar um botão para se Juntar.
- Exibir Snackbar para feedback de sucesso/erro.
