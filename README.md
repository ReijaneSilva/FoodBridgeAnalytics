# 🌱 Food Bridge Analytics

> Aplicativo Android para redução do desperdício alimentar, conectando doadores e ONGs em tempo real.

**Disciplina:** Programação para Dispositivos Móveis em Android (305)  
**Aluna:** Reijane Dantas da Silva — 202209050402  
**Período:** 2026.1 EAD

---

## 📱 Sobre o Projeto

O **Food Bridge** é uma ponte tecnológica entre doadores de excedentes alimentares (supermercados, restaurantes, padarias) e entidades receptoras (ONGs, bancos de alimentos), contribuindo para o **ODS 2 (Fome Zero)** e o **ODS 12 (Consumo Responsável)** da Agenda 2030 da ONU.

---

## ✅ Funcionalidades

### 🔐 Autenticação
- Login e cadastro com e-mail e senha (Firebase Auth)
- Três tipos de usuário: Doador, Receptor e Voluntário
- Tela de perfil com nome, e-mail, tipo e data de cadastro
- Logout com limpeza de sessão

### 🥦 Doações
- Formulário completo: alimento, quantidade, endereço de coleta, telefone e observações
- Captura automática de geolocalização via GPS (FusedLocationProviderClient)
- Nome do doador buscado automaticamente do perfil cadastrado

### 🏠 Listagem para ONGs
- Lista em tempo real via Firestore SnapshotListener
- Exibe: nome do alimento, quantidade, nome do doador, endereço de coleta, telefone e observações
- **Reserva com transação atômica** — impede que duas ONGs reservem a mesma doação simultaneamente
- Funcionamento **offline** via Room Database

### 🗺️ Mapa de Doações
- Google Maps SDK com marcadores georreferenciados
- Pins com nome do alimento e quantidade

### 📊 Dashboard de Impacto
- Métricas reais do Firestore: kg salvos, famílias assistidas, CO2 evitado, refeições estimadas
- Sistema de badges: Bronze (1+ doação), Prata (5+), Ouro (10+)
- Gráficos de distribuição de doações
- Geração de relatório em PDF

---

## 🛠️ Stack Tecnológico

| Tecnologia | Uso |
|---|---|
| Kotlin | Linguagem principal |
| Firebase Authentication | Login e cadastro |
| Cloud Firestore | Banco de dados em tempo real |
| Room Database (Jetpack) | Armazenamento offline |
| Google Maps SDK | Mapa de doações |
| FusedLocationProviderClient | Captura de GPS |
| ViewModel + LiveData | Arquitetura MVVM |
| Coroutines | Operações assíncronas |
| ViewBinding | Manipulação segura da UI |
| Android PdfDocument API | Geração de relatório PDF |
| Material Design 3 | Interface visual |

---

## 🏗️ Arquitetura

O projeto segue o padrão **MVVM (Model-View-ViewModel)**:

```
app/
├── data/
│   ├── local/          # Room Database (AppDatabase, AnalyticsDao, DoacaoDao)
│   ├── models/         # Entidades (DoacaoEntity, DonationStats, Usuario...)
│   └── remote/         # Repositórios (AnalyticsRepository, AuthRepository)
├── domain/
│   └── models/         # ImpactMetrics
├── presentation/
│   ├── adapters/        # DonationAdapter, BadgeAdapter
│   ├── ui/             # Activities e Fragments
│   ├── utils/          # PdfGenerator
│   └── viewmodel/      # AnalyticsViewModel
```

---

## 📋 Telas do App

| Tela | Descrição |
|---|---|
| LoginActivity | Login com e-mail e senha |
| RegisterActivity | Cadastro com tipo de usuário |
| SelectionActivity | Menu principal de navegação |
| DonorActivity | Publicar nova doação com GPS |
| ReceiverActivity | Listar e reservar doações |
| MapActivity | Mapa com pins de doações |
| ProfileActivity | Perfil do usuário |
| MainActivity | Dashboard de impacto e gráficos |

---

## 🚀 Como Executar

### Pré-requisitos
- Android Studio Hedgehog ou superior
- JDK 17
- Dispositivo/emulador com Android 7.0+ (API 24)

### Configuração
1. Clone o repositório:
   ```bash
   git clone https://github.com/ReijaneSilva/FoodBridgeAnalytics.git
   ```
2. Abra no Android Studio
3. Adicione o arquivo `google-services.json` na pasta `app/` (obtido no Firebase Console)
4. Adicione sua chave do Google Maps no `AndroidManifest.xml`:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="SUA_CHAVE_AQUI" />
   ```
5. Execute o projeto (`Shift + F10`)

---

## 📦 Dependências Principais

```kotlin
// Firebase
implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
implementation("com.google.firebase:firebase-auth-ktx:23.2.1")

// Room
implementation(libs.androidx.room.runtime)
implementation(libs.androidx.room.ktx)

// Google Maps e Localização
implementation("com.google.android.gms:play-services-maps:19.0.0")
implementation("com.google.android.gms:play-services-location:21.3.0")

// Material Design
implementation("com.google.android.material:material:1.9.0")

// ViewModel e Coroutines
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
```

---

## 📄 Licença

Projeto acadêmico desenvolvido para a disciplina de Programação para Dispositivos Móveis em Android — 2026.1 EAD.
