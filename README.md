# 🌱 FoodBridge Analytics

> App Android para gestão e visualização de impacto social em doações de alimentos

![Android](https://img.shields.io/badge/Android-Kotlin-green?logo=android)
![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%2B%20Auth-orange?logo=firebase)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow)

---

## 📱 Sobre o Projeto

O **FoodBridge Analytics** é um aplicativo Android desenvolvido como trabalho acadêmico para a disciplina de **Programação para Dispositivos Móveis**. O app conecta doadores de alimentos, receptores e voluntários, exibindo métricas de impacto social em tempo real a partir de dados armazenados no Firebase.

---

## ✨ Funcionalidades

| Funcionalidade | Status |
|---|---|
| 🔐 Login e Cadastro com Firebase Auth | ✅ Implementado |
| 👤 Perfis de usuário (Doador, Receptor, Voluntário) | ✅ Implementado |
| 🥗 Cadastro de doações de alimentos | ✅ Implementado |
| 📋 Listagem de doações em tempo real | ✅ Implementado |
| 📊 Dashboard de impacto social | ✅ Implementado |
| 📈 Gráficos (Pizza e Barras) | ✅ Implementado |
| 📄 Exportação de relatório em PDF | ✅ Implementado |
| 🗺️ Mapa de rotas de coleta | 🚧 Em desenvolvimento |

---

## 📊 Métricas de Impacto (Dashboard)

O app exibe automaticamente os dados calculados a partir das doações registradas:

- 🧺 **Total de Alimentos Salvos** (kg)
- 👨‍👩‍👧‍👦 **Famílias Assistidas**
- 🌍 **CO₂ Evitado** (kg)
- 🍽️ **Refeições Geradas**

---

## 🏗️ Arquitetura

```
FoodBridgeAnalytics2/
├── data/
│   ├── models/
│   │   └── Usuario.kt
│   ├── AuthRepository.kt
│   └── DonationRepository.kt
├── presentation/
│   ├── ui/
│   │   ├── LoginActivity.kt
│   │   ├── RegisterActivity.kt
│   │   ├── DonorActivity.kt
│   │   ├── ReceiverActivity.kt
│   │   ├── AnalyticsDashboardFragment.kt
│   │   └── ChartsFragment.kt
│   └── viewmodel/
│       └── AuthViewModel.kt
├── SelectionActivity.kt
└── MainActivity.kt
```

---

## 🔄 Fluxo de Navegação

```
LoginActivity
    │
    ├── (novo usuário) ──→ RegisterActivity
    │                           │
    │                           ▼
    └── (login OK) ──────→ SelectionActivity
                                │
                    ┌───────────┼───────────┐
                    ▼           ▼           ▼
               DonorActivity  ReceiverActivity  MainActivity
               (Cadastrar     (Ver doações      (Dashboard +
                doações)       em tempo real)    Gráficos)
```

---

## 🛠️ Stack Tecnológico

| Tecnologia | Versão | Uso |
|---|---|---|
| Kotlin | 2.0.21 | Linguagem principal |
| Android Gradle Plugin | 8.7.3 | Build system |
| Firebase Auth | 23.2.1 | Autenticação de usuários |
| Firebase Firestore | 25.1.4 | Banco de dados em nuvem |
| Room | 2.6.1 | Banco de dados local |
| Jetpack Compose BOM | 2024.12.01 | UI declarativa |
| Material3 | 1.3.1 | Componentes visuais |
| MPAndroidChart | 3.1.0 | Gráficos |
| ViewModel / LiveData | 2.8.7 | Arquitetura MVVM |
| Coroutines | 1.10.2 | Programação assíncrona |

---

## 🗄️ Estrutura do Firebase

### Coleção `donations`
```json
{
  "id": "uuid",
  "alimento": "Arroz",
  "quantidade": "10",
  "status": "Disponível",
  "data": 1234567890
}
```

### Coleção `usuarios`
```json
{
  "uid": "firebase_uid",
  "nome": "Nome do Usuário",
  "email": "email@exemplo.com",
  "tipoUsuario": "Doador | Receptor | Voluntário",
  "dataCadastro": 1234567890
}
```

---

## 🚀 Como Executar

### Pré-requisitos

- Android Studio Hedgehog ou superior
- JDK 17+
- Conta no [Firebase Console](https://console.firebase.google.com)

### Passos

1. **Clone o repositório**
   ```bash
   git clone https://github.com/ReijaneSilva/FoodBridgeAnalytics.git
   ```

2. **Abra no Android Studio**

3. **Configure o Firebase**
   - Crie um projeto no [Firebase Console](https://console.firebase.google.com)
   - Ative **Authentication** (Email/Senha) e **Firestore**
   - Baixe o arquivo `google-services.json` e coloque em `app/`

4. **Execute o app**
   - Conecte um dispositivo ou inicie um emulador
   - Clique em ▶️ **Run**

---

## 👩‍💻 Autora

**Reijane Dantas da Silva**
Trabalho Acadêmico — Programação para Dispositivos Móveis

---

## 📄 Licença

Este projeto foi desenvolvido para fins acadêmicos.
