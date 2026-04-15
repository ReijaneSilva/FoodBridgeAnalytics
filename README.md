# 🌱 Food Bridge

> Aplicativo Android que conecta doadores de alimentos e ONGs em tempo real, reduzindo o desperdício alimentar.

**Disciplina:** Programação para Dispositivos Móveis em Android (305)  
**Aluna:** Reijane Dantas da Silva — 202209050402  
**Período:** 2026.1 EAD  
**Repositório:** [github.com/ReijaneSilva/FoodBridgeAnalytics](https://github.com/ReijaneSilva/FoodBridgeAnalytics)

---

## 📱 Demonstração Online

> Acesse o app direto no navegador, sem instalar nada:  
> 🔗 **[Ver demo no Appetize.io](https://appetize.io/app/b_5xzfca6bddstolf4xjgypbqypu)**

---

## 🎯 Sobre o Projeto

O **Food Bridge** é uma ponte tecnológica entre doadores de excedentes alimentares e entidades receptoras, contribuindo para o **ODS 2 (Fome Zero)** e o **ODS 12 (Consumo Responsável)** da Agenda 2030 da ONU.

---

## ✅ Funcionalidades

### 🔐 Autenticação
- Login e cadastro com e-mail e senha (Firebase Auth)
- Três tipos de usuário: Doador, Receptor e Voluntário
- Tela de perfil com nome, e-mail, tipo e data de cadastro
- Logout com limpeza completa de sessão

### 🥦 Publicação de Doações (Doador)
- Formulário completo: alimento, quantidade, endereço de coleta, telefone e observações
- Captura automática de geolocalização via GPS
- Nome do doador buscado automaticamente do perfil
- Lista das próprias doações com status em tempo real
- Botão "Confirmar Coleta" quando uma ONG reserva

### 🏠 Listagem e Reserva (ONG/Receptor)
- Lista em tempo real com dados completos do doador
- Exibe: alimento, quantidade, nome do doador, endereço, telefone e observações
- Reserva com transação atômica — impede dupla reserva simultânea
- Três estados: Disponível, Reservado, Coletado
- Funciona offline via Room Database

### 🗺️ Mapa de Doações
- Google Maps SDK com marcadores georreferenciados
- Pins com nome do alimento e quantidade

### 📊 Dashboard de Impacto
- Métricas em tempo real: kg salvos, famílias assistidas, CO2 evitado, refeições estimadas
- Sistema de badges: Bronze (1+), Prata (5+), Ouro (10+)
- Gráficos de distribuição e status das doações
- Geração de relatório em PDF

---

## 🔄 Fluxo do App

```
Login/Cadastro
      ↓
Menu Principal
  ├── Sou Doador → Publicar doação (GPS + dados completos)
  │              → Ver minhas doações + Confirmar Coleta
  ├── Sou uma ONG → Listar doações disponíveis + Reservar
  ├── Ver Mapa → Pins georreferenciados das doações
  ├── Relatórios → Dashboard + Gráficos + PDF
  └── Meu Perfil → Dados do usuário
```

---

## 🛠️ Stack Tecnológico

| Tecnologia | Uso |
|---|---|
| Kotlin | Linguagem principal |
| Firebase Authentication | Login/cadastro/logout |
| Cloud Firestore | Banco remoto em tempo real + transações atômicas |
| Room Database v4 | Armazenamento offline com 12 campos |
| Google Maps SDK | Mapa com pins georreferenciados |
| FusedLocationProviderClient | Captura de GPS no cadastro |
| ViewModel + LiveData | Arquitetura MVVM |
| Coroutines | Operações assíncronas |
| ViewBinding | Acesso seguro à UI |
| Android PdfDocument API | Geração de relatório PDF |
| Material Design 3 | Interface visual |

---

## 🏗️ Arquitetura MVVM

```
app/
├── data/
│   ├── local/          # Room (AppDatabase, AnalyticsDao, DoacaoDao)
│   ├── models/         # Entidades (DoacaoEntity v4, DonationStats...)
│   └── remote/         # Repositórios (AnalyticsRepository, AuthRepository)
├── domain/
│   └── models/         # ImpactMetrics
├── presentation/
│   ├── adapters/        # DonationAdapter, BadgeAdapter
│   ├── ui/             # 9 Activities + 2 Fragments
│   ├── utils/          # PdfGenerator
│   └── viewmodel/      # AnalyticsViewModel
└── test/
    └── DoacaoEntityTest # 3 testes unitários passando
```

---

## 📋 Telas Implementadas

| Tela | Descrição |
|---|---|
| LoginActivity | Login com e-mail e senha |
| RegisterActivity | Cadastro com tipo de usuário |
| SelectionActivity | Menu principal de navegação |
| DonorActivity | Publicar doação + lista das minhas doações |
| ReceiverActivity | Listar e reservar doações |
| MapActivity | Mapa com pins georreferenciados |
| ProfileActivity | Perfil do usuário |
| MainActivity | Dashboard de impacto e gráficos |

---

## 🚀 Como Executar

### Pré-requisitos
- Android Studio Hedgehog ou superior
- JDK 17
- Android 7.0+ (API 24)

### Configuração

1. Clone o repositório:
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

## 🧪 Testes Unitários

```bash
./gradlew test
```

3 testes passando em `DoacaoEntityTest`:
- status padrão "Disponível" ao criar doação
- campo `sincronizado` falso por padrão
- campos opcionais nulos por padrão

---

## 📦 Gerar APK

```
Build > Build Bundle(s)/APK(s) > Build APK(s)
```
APK gerado em: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🔒 Segurança

- Chave do Google Maps protegida via `local.properties`
- Regras do Firestore com autenticação obrigatória
- Transações atômicas para controle de reservas concorrentes

---

## 📄 Licença

Projeto acadêmico — Programação para Dispositivos Móveis em Android — 2026.1 EAD
