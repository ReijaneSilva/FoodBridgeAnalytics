# Food Bridge

> Aplicativo Android que conecta doadores de alimentos e ONGs em tempo real, reduzindo o desperdicio alimentar.

**Disciplina:** Programacao para Dispositivos Moveis em Android (305)  
**Aluna:** Reijane Dantas da Silva — 202209050402  
**Periodo:** 2026.1 EAD  
**Repositorio:** [github.com/ReijaneSilva/FoodBridgeAnalytics](https://github.com/ReijaneSilva/FoodBridgeAnalytics)

---

## Demonstracao Online

Acesse o app direto no navegador, sem instalar nada:  
**[Ver demo no Appetize.io](https://appetize.io/app/b_5xzfca6bddstolf4xjgypbqypu)**

---

## Sobre o Projeto

O Food Bridge e uma ponte tecnologica entre doadores de excedentes alimentares e entidades receptoras, contribuindo para o ODS 2 (Fome Zero) e o ODS 12 (Consumo Responsavel) da Agenda 2030 da ONU.

---

## Funcionalidades

### Autenticacao
- Login e cadastro com e-mail e senha via Firebase Auth
- Tres tipos de usuario: Doador, Receptor e Voluntario
- Tela de perfil com nome, e-mail, tipo e data de cadastro
- Logout com limpeza completa de sessao

### Publicacao de Doacoes (Doador)
- Formulario completo: alimento, quantidade, endereco de coleta, telefone e observacoes
- Captura automatica de geolocalizacao via GPS
- Nome do doador buscado automaticamente do perfil
- Lista das proprias doacoes com status em tempo real
- Botao "Confirmar Coleta" quando uma ONG reserva

### Listagem e Reserva (ONG/Receptor)
- Lista em tempo real com dados completos do doador
- Exibe: alimento, quantidade, nome do doador, endereco, telefone e observacoes
- Reserva com transacao atomica — impede dupla reserva simultanea
- Tres estados: Disponivel, Reservado, Coletado
- Funciona offline via Room Database

### Mapa de Doacoes
- Google Maps SDK com marcadores georreferenciados
- Pins com nome do alimento e quantidade

### Dashboard de Impacto
- Metricas em tempo real: kg salvos, familias assistidas, CO2 evitado, refeicoes estimadas
- Sistema de badges: Bronze (1+), Prata (5+), Ouro (10+)
- Graficos de distribuicao e status das doacoes
- Geracao de relatorio em PDF

---

## Fluxo do App

```
Login/Cadastro
      |
Menu Principal
  |-- Sou Doador -> Publicar doacao (GPS + dados completos)
  |              -> Ver minhas doacoes + Confirmar Coleta
  |-- Sou uma ONG -> Listar doacoes disponiveis + Reservar
  |-- Ver Mapa -> Pins georreferenciados das doacoes
  |-- Relatorios -> Dashboard + Graficos + PDF
  |-- Meu Perfil -> Dados do usuario
```

---

## Stack Tecnologico

| Tecnologia | Uso |
|---|---|
| Kotlin | Linguagem principal |
| Firebase Authentication | Login/cadastro/logout |
| Cloud Firestore | Banco remoto em tempo real + transacoes atomicas |
| Room Database v4 | Armazenamento offline com 12 campos |
| Google Maps SDK | Mapa com pins georreferenciados |
| FusedLocationProviderClient | Captura de GPS no cadastro |
| ViewModel + LiveData | Arquitetura MVVM |
| Coroutines | Operacoes assincronas |
| ViewBinding | Acesso seguro a UI |
| Android PdfDocument API | Geracao de relatorio PDF |
| Material Design 3 | Interface visual |

---

## Arquitetura MVVM

```
app/
|-- data/
|   |-- local/          # Room (AppDatabase, AnalyticsDao, DoacaoDao)
|   |-- models/         # Entidades (DoacaoEntity v4, DonationStats...)
|   |-- remote/         # Repositorios (AnalyticsRepository, AuthRepository)
|-- domain/
|   |-- models/         # ImpactMetrics
|-- presentation/
|   |-- adapters/       # DonationAdapter, BadgeAdapter
|   |-- ui/             # 9 Activities + 2 Fragments
|   |-- utils/          # PdfGenerator
|   |-- viewmodel/      # AnalyticsViewModel
|-- test/
    |-- DoacaoEntityTest # 3 testes unitarios passando
```

---

## Telas Implementadas

| Tela | Descricao |
|---|---|
| LoginActivity | Login com e-mail e senha |
| RegisterActivity | Cadastro com tipo de usuario |
| SelectionActivity | Menu principal de navegacao |
| DonorActivity | Publicar doacao + lista das minhas doacoes |
| ReceiverActivity | Listar e reservar doacoes |
| MapActivity | Mapa com pins georreferenciados |
| ProfileActivity | Perfil do usuario |
| MainActivity | Dashboard de impacto e graficos |

---

## Como Executar

### Pre-requisitos
- Android Studio Hedgehog ou superior
- JDK 17
- Android 7.0+ (API 24)

### Configuracao

1. Clone o repositorio:
```bash
git clone https://github.com/ReijaneSilva/FoodBridgeAnalytics.git
```

2. Abra no Android Studio

3. Crie o arquivo `local.properties` na raiz com sua chave:
```
MAPS_API_KEY=sua_chave_google_maps_aqui
```

4. Adicione o `google-services.json` na pasta `app/`

5. Execute com `Shift + F10`

---

## Testes Unitarios

```bash
./gradlew test
```

3 testes passando em `DoacaoEntityTest`:
- status padrao Disponivel ao criar doacao
- campo sincronizado falso por padrao
- campos opcionais nulos por padrao

---

## Gerar APK

```
Build > Build Bundle(s)/APK(s) > Build APK(s)
```
APK gerado em: `app/build/outputs/apk/debug/app-debug.apk`

---

## Seguranca

- Chave do Google Maps protegida via `local.properties`
- Regras do Firestore com autenticacao obrigatoria
- Transacoes atomicas para controle de reservas concorrentes

---

## Licenca

Projeto academico — Programacao para Dispositivos Moveis em Android — 2026.1 EAD
