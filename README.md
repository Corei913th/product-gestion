# API REST – Gestion de Produits

API REST développée avec **Spring Boot** et **Java 21**, appliquant les principes de **Clean Code**, une **architecture en couches stricte**, une **gestion uniforme des exceptions**, une **suite complète de tests automatisés** mesurée avec **JaCoCo**, et une **documentation OpenAPI (Swagger)**.

---

## 1. Architecture du Projet

L'application suit une séparation stricte des responsabilités :

```
eadl.productgestion
├── config/              # Configuration OpenAPI (Swagger)
├── controller/          # Contrôleurs REST (@Valid, statuts HTTP, délégation aux services)
├── dto/
│   ├── request/         # Records d'entrée avec Bean Validation (@NotBlank, @Positive, etc.)
│   └── response/        # Records de sortie et formats d'erreur standard (ApiError, FieldViolation)
├── entity/              # Entités JPA (Category, Product) jamais exposées directement
├── exception/           # Hiérarchie BusinessException et GlobalExceptionHandler
├── mappers/             # Mappers MapStruct
├── repositories/        # Interfaces Spring Data JPA et ProductSpecification
└── services/            # Logique métier, règles RG1-RG8, interfaces documentées (Javadoc)
    └── impl/            # Implémentations injectées par constructeur (champs final)
```

---

## 2. Fonctionnalités et Règles de Gestion

### Fonctionnalités
- **F1 - Créer un produit** : Enregistrement avec nom, référence SKU unique, prix, stock initial et catégorie rattachée.
- **F2 - Consulter un produit** : Recherche par identifiant.
- **F3 - Lister les produits** : Recherche multicritère (nom, catégorie, fourchette de prix), pagination et tri.
- **F4 - Modifier un produit** : Remplacement des données avec vérification d'unicité du SKU.
- **F5 - Ajuster le stock** : Variation relative positive (entrée) ou négative (sortie).
- **F6 - Supprimer un produit** : Suppression par identifiant avec erreur 404 explicite si inexistant.
- **F7 - Gérer les catégories** : Création avec nom unique et consultation.

### Règles de Gestion (RG1 à RG8)
- **RG1** : Nom obligatoire, entre 3 et 100 caractères.
- **RG2** : SKU obligatoire, unique, format `^[A-Z0-9-]{3,20}$`.
- **RG3** : Prix strictement positif (`BigDecimal`, deux décimales).
- **RG4** : Quantité en stock entière $\ge$ 0.
- **RG5** : Catégorie obligatoire et existante (`CategoryNotFoundException` sinon).
- **RG6** : Une sortie de stock ne peut pas dépasser le stock disponible (`InsufficientStockException` sinon).
- **RG7** : Erreurs 404 explicites et uniformes (`ProductNotFoundException`, `CategoryNotFoundException`).
- **RG8** : Description facultative limitée à 500 caractères.

---

## 3. Endpoints de l'API REST (`/api/v1`)

| Méthode | URI | Description | Statuts HTTP |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/v1/categories` | Créer une catégorie | `201 Created`, `400`, `409` |
| **GET** | `/api/v1/categories` | Lister les catégories | `200 OK` |
| **GET** | `/api/v1/categories/{id}` | Consulter une catégorie | `200 OK`, `404` |
| **POST** | `/api/v1/products` | Créer un produit | `201 Created`, `400`, `404`, `409` |
| **GET** | `/api/v1/products` | Lister les produits (page, sort, name, categoryId, minPrice, maxPrice) | `200 OK`, `400` |
| **GET** | `/api/v1/products/{id}` | Consulter un produit | `200 OK`, `404` |
| **PUT** | `/api/v1/products/{id}` | Modifier un produit | `200 OK`, `400`, `404`, `409` |
| **PATCH** | `/api/v1/products/{id}/stock` | Ajuster le stock (`delta` positif ou négatif) | `200 OK`, `400`, `404`, `409` |
| **DELETE** | `/api/v1/products/{id}` | Supprimer un produit | `204 No Content`, `404` |
| **POST** | `/api/v1/users` | Créer un utilisateur | `201 Created`, `400`, `409` |
| **GET** | `/api/v1/users` | Lister tous les utilisateurs | `200 OK` |
| **GET** | `/api/v1/users/{id}` | Consulter un utilisateur | `200 OK`, `404` |
| **PUT** | `/api/v1/users/{id}` | Modifier un utilisateur | `200 OK`, `400`, `404`, `409` |
| **PATCH** | `/api/v1/users/{id}/status` | Modifier le statut (ACTIVE / BLOCKED) | `200 OK`, `404` |
| **DELETE** | `/api/v1/users/{id}` | Supprimer un utilisateur | `204 No Content`, `404` |

---

## 4. Documentation Swagger (OpenAPI)

Une fois l'application lancée :
- **Interface Swagger UI** : [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Contrat OpenAPI JSON** : [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 5. Prérequis & Lancement

### Prérequis
- **Java 21 (LTS)** ou supérieur.
- **PostgreSQL 14+** (port 5432, base `product_gestion_db`, utilisateur `postgres` / mot de passe `root`).
- **Maven 3.9+** (ou wrapper `mvnw`).

### Lancer l'application
```bash
mvn spring-boot:run
```

---

## 6. Tests & Couverture de Code

### Exécuter l'ensemble des tests
```bash
mvn test
```

### Générer le rapport de couverture JaCoCo
```bash
mvn clean verify
```
Le rapport HTML est accessible dans : `target/site/jacoco/index.html`.
- **Couverture sur les Services** : $\ge 95\%$ (objectif minimum : 90%).
- **Couverture globale** : $\ge 80\%$.

### Analyse Qualité avec SonarQube
```bash
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=<VOTRE_JETON>
```
