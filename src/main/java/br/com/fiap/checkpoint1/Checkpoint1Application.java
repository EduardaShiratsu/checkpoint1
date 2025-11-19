package br.com.fiap.checkpoint1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Checkpoint1Application {

	public static void main(String[] args) {
		SpringApplication.run(Checkpoint1Application.class, args);
	}



}


import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';

const baseUrl = 'https://vida-equilibrio-api-69a356625afd.herokuapp.com';

/// =========================================================
/// APP
/// =========================================================
void main() {
  runApp(const VidaEquilibrioApp());
}

class VidaEquilibrioApp extends StatelessWidget {
  const VidaEquilibrioApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Vida Equilíbrio',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorSchemeSeed: Colors.teal,
        useMaterial3: true,
      ),
      home: const UserGateKeeper(),
    );
  }
}

/// =========================================================
/// TELA DE DECISÃO (VERIFICA SE EXISTE USERID)
/// =========================================================
class UserGateKeeper extends StatefulWidget {
  const UserGateKeeper({super.key});

  @override
  State<UserGateKeeper> createState() => _UserGateKeeperState();
}

class _UserGateKeeperState extends State<UserGateKeeper> {
  @override
  void initState() {
    super.initState();
    _redirect();
  }

  Future<void> _redirect() async {
    final prefs = await SharedPreferences.getInstance();
    final id = prefs.getString("uid");

    await Future.delayed(const Duration(milliseconds: 400));

    if (!mounted) return;

    if (id == null || id.trim().isEmpty) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const UserIdInputPage()),
      );
    } else {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const ActivitiesHome()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(
        child: CircularProgressIndicator(),
      ),
    );
  }
}

/// =========================================================
/// TELA PARA DEFINIR / ALTERAR USER
/// =========================================================
class UserIdInputPage extends StatefulWidget {
  const UserIdInputPage({super.key});

  @override
  State<UserIdInputPage> createState() => _UserIdInputPageState();
}

class _UserIdInputPageState extends State<UserIdInputPage> {
  final TextEditingController _idCtrl = TextEditingController();
  final _formKey = GlobalKey<FormState>();

  @override
  void initState() {
    super.initState();
    _loadSaved();
  }

  Future<void> _loadSaved() async {
    final prefs = await SharedPreferences.getInstance();
    _idCtrl.text = prefs.getString("uid") ?? '';
    setState(() {});
  }

  Future<void> _saveId() async {
    if (!_formKey.currentState!.validate()) return;

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString("uid", _idCtrl.text.trim());

    if (!mounted) return;

    Navigator.pushReplacement(
      context,
      MaterialPageRoute(builder: (_) => const ActivitiesHome()),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Identificação")),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                controller: _idCtrl,
                decoration: const InputDecoration(
                  labelText: "Digite seu userId",
                  border: OutlineInputBorder(),
                ),
                validator: (value) =>
                    (value == null || value.trim().isEmpty)
                        ? "Este campo é obrigatório"
                        : null,
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: _saveId,
                child: const Text("Confirmar"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// =========================================================
/// MODELO DE ATIVIDADE
/// =========================================================
class Activity {
  final int id;
  final String nome;
  final String descricao;
  final String data;

  Activity({
    required this.id,
    required this.nome,
    required this.descricao,
    required this.data,
  });

  factory Activity.fromJson(Map<String, dynamic> map) {
    return Activity(
      id: int.tryParse(map["id"].toString()) ?? 0,
      nome: map["titulo"]?.toString() ?? "",
      descricao: map["descricao"]?.toString() ?? "",
      data: map["data"]?.toString() ?? "",
    );
  }
}

/// =========================================================
/// TELA DE LISTAGEM
/// =========================================================
class ActivitiesHome extends StatefulWidget {
  const ActivitiesHome({super.key});

  @override
  State<ActivitiesHome> createState() => _ActivitiesHomeState();
}

class _ActivitiesHomeState extends State<ActivitiesHome> {
  late Future<List<Activity>> _futureList;

  @override
  void initState() {
    super.initState();
    _futureList = _fetchActivities();
  }

  Future<String> _getUser() async {
    final prefs = await SharedPreferences.getInstance();
    final id = prefs.getString("uid") ?? "";
    if (id.isEmpty) throw Exception("UserId não definido");
    return id;
  }

  Future<List<Activity>> _fetchActivities() async {
    final user = await _getUser();

    final uri = Uri.parse("$baseUrl/atividades")
        .replace(queryParameters: {"criador": user});

    final response = await http.get(uri);

    if (response.statusCode != 200) {
      throw Exception("Falha ao carregar atividades");
    }

    final decoded = jsonDecode(response.body);
    if (decoded is List) {
      return decoded.map((e) => Activity.fromJson(e)).toList();
    }
    return [];
  }

  Future<void> _delete(int id) async {
    final uri = Uri.parse("$baseUrl/atividades/$id");
    final res = await http.delete(uri);

    if (!mounted) return;

    if (res.statusCode == 200 || res.statusCode == 204) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Removido com sucesso")),
      );
      setState(() => _futureList = _fetchActivities());
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Erro ao excluir (${res.statusCode})")),
      );
    }
  }

  Future<void> _navigateToForm([Activity? act]) async {
    final changed = await Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => ActivityFormScreen(initial: act)),
    );

    if (changed == true) {
      setState(() => _futureList = _fetchActivities());
    }
  }

  Future<void> _switchUser() async {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => const UserIdInputPage()),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Minhas atividades"),
        actions: [
          IconButton(
            onPressed: _switchUser,
            icon: const Icon(Icons.person),
          )
        ],
      ),
      body: FutureBuilder<List<Activity>>(
        future: _futureList,
        builder: (_, snap) {
          if (snap.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }
          if (snap.hasError) {
            return Center(child: Text("Erro: ${snap.error}"));
          }

          final list = snap.data ?? [];
          if (list.isEmpty) {
            return const Center(child: Text("Nenhuma atividade encontrada."));
          }

          return ListView.builder(
            itemCount: list.length,
            itemBuilder: (_, i) {
              final a = list[i];
              return Dismissible(
                key: Key(a.id.toString()),
                direction: DismissDirection.endToStart,
                background: Container(
                  alignment: Alignment.centerRight,
                  padding: const EdgeInsets.only(right: 16),
                  child: const Icon(Icons.delete),
                ),
                confirmDismiss: (_) async {
                  return await showDialog(
                    context: context,
                    builder: (_) => AlertDialog(
                      title: const Text("Confirmar"),
                      content: const Text(
                          "Tem certeza que deseja excluir esta atividade?"),
                      actions: [
                        TextButton(
                          onPressed: () => Navigator.pop(context, false),
                          child: const Text("Cancelar"),
                        ),
                        TextButton(
                          onPressed: () => Navigator.pop(context, true),
                          child: const Text("Excluir"),
                        ),
                      ],
                    ),
                  );
                },
                onDismissed: (_) => _delete(a.id),
                child: ListTile(
                  title: Text(a.nome),
                  subtitle: Text("${a.descricao}\nData: ${a.data}"),
                  isThreeLine: true,
                  onTap: () => _navigateToForm(a),
                ),
              );
            },
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _navigateToForm,
        child: const Icon(Icons.add),
      ),
    );
  }
}

/// =========================================================
/// FORMULÁRIO
/// =========================================================
class ActivityFormScreen extends StatefulWidget {
  final Activity? initial;

  const ActivityFormScreen({super.key, this.initial});

  @override
  State<ActivityFormScreen> createState() => _ActivityFormScreenState();
}

class _ActivityFormScreenState extends State<ActivityFormScreen> {
  final _key = GlobalKey<FormState>();
  final _tituloCtrl = TextEditingController();
  final _descCtrl = TextEditingController();
  final _dataCtrl = TextEditingController();

  bool sending = false;

  @override
  void initState() {
    super.initState();

    if (widget.initial != null) {
      _tituloCtrl.text = widget.initial!.nome;
      _descCtrl.text = widget.initial!.descricao;
      _dataCtrl.text = widget.initial!.data;
    }
  }

  Future<String> _user() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString("uid") ?? "";
  }

  Future<void> _submit() async {
    if (!_key.currentState!.validate()) return;

    setState(() => sending = true);

    try {
      final userId = await _user();

      final body = {
        "titulo": _tituloCtrl.text.trim(),
        "descricao": _descCtrl.text.trim(),
        "data": _dataCtrl.text.trim(),
        "criador": userId,
      };

      final uri = Uri.parse("$baseUrl/atividades");

      final res = await http.post(
        uri,
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(body),
      );

      if (!mounted) return;

      if (res.statusCode == 200 || res.statusCode == 201) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("Atividade salva!")),
        );
        Navigator.pop(context, true);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Erro (${res.statusCode})")),
        );
      }
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Erro: $e")),
      );
    } finally {
      if (mounted) setState(() => sending = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final editing = widget.initial != null;

    return Scaffold(
      appBar: AppBar(
        title: Text(editing ? "Editar atividade" : "Nova atividade"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _key,
          child: ListView(
            children: [
              TextFormField(
                controller: _tituloCtrl,
                decoration: const InputDecoration(
                  labelText: "Título",
                  border: OutlineInputBorder(),
                ),
                validator: (v) =>
                    v == null || v.trim().isEmpty ? "Informe o título" : null,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _descCtrl,
                maxLines: 3,
                decoration: const InputDecoration(
                  labelText: "Descrição",
                  border: OutlineInputBorder(),
                ),
                validator: (v) => (v == null || v.trim().isEmpty)
                    ? "Informe a descrição"
                    : null,
              ),
              const SizedBox(height: 12),
              TextFormField(
                controller: _dataCtrl,
                decoration: const InputDecoration(
                  labelText: "Data (AAAA-MM-DD)",
                  border: OutlineInputBorder(),
                ),
                validator: (v) =>
                    v == null || v.trim().isEmpty ? "Informe a data" : null,
              ),
              const SizedBox(height: 12),
              ElevatedButton(
                onPressed: sending ? null : _submit,
                child: sending
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Text("Salvar"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
