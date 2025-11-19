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



main.dart

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

const String apiBaseUrl =
    'https://vida-equilibrio-api-69a356625afd.herokuapp.com';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Vida Equilíbrio',
      theme: ThemeData(
        useMaterial3: true,
        colorSchemeSeed: Colors.red,
      ),
      home: const SplashDecideUserId(),
    );
  }
}

/// Decide se vai pra tela de informe de userId ou direto pra lista
class SplashDecideUserId extends StatefulWidget {
  const SplashDecideUserId({super.key});

  @override
  State<SplashDecideUserId> createState() => _SplashDecideUserIdState();
}

class _SplashDecideUserIdState extends State<SplashDecideUserId> {
  @override
  void initState() {
    super.initState();
    _checkUserId();
  }

  Future<void> _checkUserId() async {
    final prefs = await SharedPreferences.getInstance();
    final userId = prefs.getString('userId');
    if (!mounted) return;
    if (userId == null || userId.isEmpty) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const UserIdPage()),
      );
    } else {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const ActivitiesPage()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(child: CircularProgressIndicator()),
    );
  }
}

/// Tela para informar / alterar userId
class UserIdPage extends StatefulWidget {
  const UserIdPage({super.key});

  @override
  State<UserIdPage> createState() => _UserIdPageState();
}

class _UserIdPageState extends State<UserIdPage> {
  final _formKey = GlobalKey<FormState>();
  final _controller = TextEditingController();

  @override
  void initState() {
    super.initState();
    _loadUserId();
  }

  Future<void> _loadUserId() async {
    final prefs = await SharedPreferences.getInstance();
    _controller.text = prefs.getString('userId') ?? '';
  }

  Future<void> _saveAndGo() async {
    if (!_formKey.currentState!.validate()) return;

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('userId', _controller.text.trim());

    if (!mounted) return;
    Navigator.pushReplacement(
      context,
      MaterialPageRoute(builder: (_) => const ActivitiesPage()),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Informe seu userId'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                controller: _controller,
                decoration: const InputDecoration(
                  labelText: 'userId',
                  border: OutlineInputBorder(),
                ),
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Informe um userId';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 16),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _saveAndGo,
                  child: const Text('Salvar'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// Modelo simples de atividade (ajuste campos conforme a API)
class Activity {
  final int id;
  final String titulo;
  final String descricao;
  final String data;

  Activity({
    required this.id,
    required this.titulo,
    required this.descricao,
    required this.data,
  });

  factory Activity.fromJson(Map<String, dynamic> json) {
    return Activity(
      id: json['id'] is int
          ? json['id']
          : int.tryParse(json['id'].toString()) ?? 0,
      titulo: json['titulo']?.toString() ?? '',
      descricao: json['descricao']?.toString() ?? '',
      data: json['data']?.toString() ?? '',
    );
  }
}

/// Tela "Minhas atividades"
class ActivitiesPage extends StatefulWidget {
  const ActivitiesPage({super.key});

  @override
  State<ActivitiesPage> createState() => _ActivitiesPageState();
}

class _ActivitiesPageState extends State<ActivitiesPage> {
  late Future<List<Activity>> _futureActivities;

  @override
  void initState() {
    super.initState();
    _futureActivities = _loadActivities();
  }

  Future<String> _getUserId() async {
    final prefs = await SharedPreferences.getInstance();
    final userId = prefs.getString('userId');
    if (userId == null || userId.isEmpty) {
      throw Exception('userId não configurado');
    }
    return userId;
  }

  Future<List<Activity>> _loadActivities() async {
    final userId = await _getUserId();
    final uri = Uri.parse('$apiBaseUrl/atividades')
        .replace(queryParameters: {'criador': userId});

    final resp = await http.get(uri);
    if (resp.statusCode != 200) {
      throw Exception('Erro ao buscar atividades (${resp.statusCode})');
    }

    final data = json.decode(resp.body);
    if (data is List) {
      return data.map((e) => Activity.fromJson(e)).toList();
    } else {
      return [];
    }
  }

  Future<void> _deleteActivity(int id) async {
    try {
      final uri = Uri.parse('$apiBaseUrl/atividades/$id');
      final resp = await http.delete(uri);

      if (!mounted) return;

      if (resp.statusCode == 200 || resp.statusCode == 204) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Atividade excluída com sucesso')),
        );
        setState(() {
          _futureActivities = _loadActivities();
        });
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
              content:
                  Text('Erro ao excluir atividade (${resp.statusCode})')),
        );
      }
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Erro: $e')),
      );
    }
  }

  Future<void> _goToForm([Activity? activity]) async {
    final changed = await Navigator.push<bool>(
      context,
      MaterialPageRoute(
        builder: (_) => ActivityFormPage(activity: activity),
      ),
    );

    if (changed == true) {
      setState(() {
        _futureActivities = _loadActivities();
      });
    }
  }

  Future<void> _changeUserId() async {
    await Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => const UserIdPage()),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Minhas atividades'),
        actions: [
          IconButton(
            onPressed: _changeUserId,
            icon: const Icon(Icons.person),
            tooltip: 'Alterar userId',
          ),
        ],
      ),
      body: FutureBuilder<List<Activity>>(
        future: _futureActivities,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }
          if (snapshot.hasError) {
            return Center(
              child: Text('Erro: ${snapshot.error}'),
            );
          }

          final activities = snapshot.data ?? [];

          if (activities.isEmpty) {
            return const Center(child: Text('Nenhuma atividade encontrada.'));
          }

          return ListView.builder(
            itemCount: activities.length,
            itemBuilder: (context, index) {
              final a = activities[index];
              return Dismissible(
                key: ValueKey(a.id),
                direction: DismissDirection.endToStart,
                confirmDismiss: (_) async {
                  return await showDialog<bool>(
                        context: context,
                        builder: (_) => AlertDialog(
                          title: const Text('Excluir atividade'),
                          content: const Text(
                              'Tem certeza que deseja excluir esta atividade?'),
                          actions: [
                            TextButton(
                              onPressed: () => Navigator.pop(context, false),
                              child: const Text('Cancelar'),
                            ),
                            TextButton(
                              onPressed: () => Navigator.pop(context, true),
                              child: const Text('Excluir'),
                            ),
                          ],
                        ),
                      ) ??
                      false;
                },
                onDismissed: (_) => _deleteActivity(a.id),
                background: Container(
                  alignment: Alignment.centerRight,
                  padding: const EdgeInsets.only(right: 16),
                  child: const Icon(Icons.delete),
                ),
                child: ListTile(
                  title: Text(a.titulo),
                  subtitle: Text('${a.descricao}\nData: ${a.data}'),
                  isThreeLine: true,
                  onTap: () => _goToForm(a),
                ),
              );
            },
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _goToForm,
        child: const Icon(Icons.add),
      ),
    );
  }
}

/// Tela de formulário de cadastro de atividade
class ActivityFormPage extends StatefulWidget {
  final Activity? activity;
  const ActivityFormPage({super.key, this.activity});

  @override
  State<ActivityFormPage> createState() => _ActivityFormPageState();
}

class _ActivityFormPageState extends State<ActivityFormPage> {
  final _formKey = GlobalKey<FormState>();
  final _tituloController = TextEditingController();
  final _descricaoController = TextEditingController();
  final _dataController = TextEditingController();

  bool _saving = false;

  @override
  void initState() {
    super.initState();
    if (widget.activity != null) {
      _tituloController.text = widget.activity!.titulo;
      _descricaoController.text = widget.activity!.descricao;
      _dataController.text = widget.activity!.data;
    }
  }

  Future<String> _getUserId() async {
    final prefs = await SharedPreferences.getInstance();
    final userId = prefs.getString('userId');
    if (userId == null || userId.isEmpty) {
      throw Exception('userId não configurado');
    }
    return userId;
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _saving = true);

    try {
      final userId = await _getUserId();

      final body = {
        // CAMPOS OBRIGATÓRIOS - AJUSTE CONFORME A API
        'titulo': _tituloController.text.trim(),
        'descricao': _descricaoController.text.trim(),
        'data': _dataController.text.trim(),
        'criador': userId,
      };

      final uri = Uri.parse('$apiBaseUrl/atividades');
      final resp = await http.post(
        uri,
        headers: {'Content-Type': 'application/json'},
        body: json.encode(body),
      );

      if (!mounted) return;

      if (resp.statusCode == 201 || resp.statusCode == 200) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Atividade salva com sucesso')),
        );
        Navigator.pop(context, true); // avisa que mudou
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
              content:
                  Text('Erro ao salvar atividade (${resp.statusCode})')),
        );
      }
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Erro: $e')),
      );
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final isEdit = widget.activity != null;

    return Scaffold(
      appBar: AppBar(
        title: Text(isEdit ? 'Editar atividade' : 'Nova atividade'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(
                controller: _tituloController,
                decoration: const InputDecoration(
                  labelText: 'Título',
                  border: OutlineInputBorder(),
                ),
                validator: (v) =>
                    v == null || v.trim().isEmpty ? 'Informe o título' : null,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _descricaoController,
                decoration: const InputDecoration(
                  labelText: 'Descrição',
                  border: OutlineInputBorder(),
                ),
                maxLines: 3,
                validator: (v) => v == null || v.trim().isEmpty
                    ? 'Informe a descrição'
                    : null,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _dataController,
                decoration: const InputDecoration(
                  labelText: 'Data (AAAA-MM-DD, por ex.)',
                  border: OutlineInputBorder(),
                ),
                validator: (v) =>
                    v == null || v.trim().isEmpty ? 'Informe a data' : null,
              ),
              const SizedBox(height: 16),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _saving ? null : _submit,
                  child: _saving
                      ? const SizedBox(
                          height: 18,
                          width: 18,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        )
                      : const Text('Salvar'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}



pubspec.yaml
dependencies:
  flutter:
    sdk: flutter
  http: ^1.1.0
  shared_preferences: ^2.2.0
