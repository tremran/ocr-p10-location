# Cahier des charges fonctionnels

## Objet du document

Ce document définit les besoins fonctionnels de la nouvelle application Your Car Your Way. L’objectif est de fournir une application client centralisée, accessible à l’international, pour consulter les offres, réserver un véhicule et gérer ses réservations.

Les exigences sont exprimées du point de vue des utilisateurs et des systèmes externes. Les choix de technologie, d’hébergement et d’architecture détaillée feront l’objet de documents techniques dédiés.

## Contexte et objectifs

Your Car Your Way dispose actuellement de plusieurs applications nationales développées avec des technologies et des règles métier hétérogènes. Cette situation entraîne notamment :

- une expérience utilisateur différente selon le pays ;
- des modèles de données et des règles de réservation divergents ;
- des échanges de données limités entre les applications ;
- des niveaux de disponibilité, de sécurité et de fiabilité inégaux ;
- des opérations de déploiement et de maintenance difficiles à homogénéiser.

La nouvelle application doit :

- centraliser l’expérience client pour tous les pays couverts ;
- proposer un parcours homogène de recherche et de réservation ;
- conserver la possibilité d’appliquer des règles et des données propres à chaque pays ;
- exposer une API exploitable par les applications utilisées en agence ;
- améliorer la sécurité, la disponibilité et la capacité d’évolution du service.

## Périmètre

### Inclus dans la V1

- l’espace client ;
- la consultation et la modification du profil ;
- la consultation des agences ;
- la recherche d’offres de location ;
- la consultation du détail d’une offre ;
- la réservation et le paiement en ligne ;
- la consultation, la modification et l’annulation des réservations ;
- l’historique des réservations passées et en cours ;
- le tchat en temps réel avec le support ou une agence ;
- l’API destinée aux applications en agence ;
- la prise en compte des catégories de véhicules selon la norme ACRISS.

### Hors périmètre de la V1

- les écrans métiers utilisés par les employés en agence ;
- la gestion opérationnelle interne des véhicules et des agences ;
- la gestion du catalogue tarifaire par les employés ;
- l’encaissement directement opéré par l’application ;
- les fonctionnalités non décrites dans ce document.

Les applications en agence restent hors du périmètre de l’interface client, mais doivent pouvoir accéder aux données nécessaires au moyen de l’API.

## Acteurs et systèmes externes

### Client

Le client peut gérer son profil, rechercher une offre, réserver un véhicule, payer et gérer ses réservations.

### Application d’agence

Une application d’agence consomme l’API pour consulter et modifier les données métier autorisées. Elle doit pouvoir effectuer les opérations CRUD standard pour chaque domaine exposé, notamment les utilisateurs et les réservations.

### Prestataire de paiement

Un prestataire externe de paiement en ligne, par exemple Stripe, prend en charge la saisie et le traitement des paiements. L’application ne doit pas stocker les données bancaires sensibles du client.

### Service de notification

Le système doit pouvoir notifier le client des événements importants d’une réservation, notamment sa confirmation, sa modification, son annulation ou l’échec d’un paiement. Le canal de notification à retenir reste à préciser.

### Conseiller ou agent d’agence

Un conseiller habilité peut répondre aux demandes des clients depuis l’outil de support ou l’application d’agence. Il ne peut consulter que les conversations et les données nécessaires à la prise en charge de la demande.

## Parcours fonctionnels

### Epic 1 - Gestion du compte client

Objectif : Permettre au client d'identifier, sécuriser et piloter son compte personnel au sein de l'application.

#### US#01 - Consulter son profil

En tant que client authentifié, je veux consulter mon profil, afin de vérifier mes informations personnelles.

**Critères d'acceptation :**

- Le profil affiche le nom, le prénom, la date de naissance et l'adresse du client.
- L'accès au profil est réservé au client authentifié concerné.

#### US#02 - Modifier son profil

En tant que client authentifié, je veux modifier mes informations personnelles, afin de maintenir mon profil à jour et de les réutiliser lors d'une réservation.

**Critères d'acceptation :**

- Le client peut modifier son nom, son prénom, sa date de naissance et son adresse.
- Les données sont validées coté front et coté back avant leur enregistrement.
- Le client est informé du succès ou de l'échec de la modification.

#### US#03 - Supprimer son compte

En tant que client authentifié, je veux demander la suppression de mon compte, afin de maîtriser mes données personnelles.

**Critères d'acceptation :**

- Une confirmation explicite est demandée avant la suppression.
- Le mot de passe du compte doit être saisi et vérifié.
- La suppression est refusée si le mot de passe est incorrect.

### Epic 2 - Découverte des agences et des offres

Objectif : faciliter la recherche d'une agence et la découverte des offres disponibles, avec une information claire et exploitable.

#### US#04 - Consulter les agences

En tant que client, je veux consulter et rechercher les agences, afin de choisir les lieux de départ et de retour adaptés à mon trajet.

**Critères d'acceptation :**

- Chaque agence affiche son nom ou son identifiant, son adresse, sa ville et son pays.
- Les coordonnées géographiques ou un moyen de localiser l'agence sont affichés lorsqu'ils sont disponibles.
- Les horaires d'ouverture sont affichés lorsqu'ils sont disponibles.
- La liste peut être recherchée ou filtrée par ville et par pays.

#### US#05 - Rechercher une offre

En tant que client, je veux rechercher une offre selon mes lieux, dates, heures et catégorie de véhicule, afin de trouver un véhicule disponible pour mon trajet.

**Critères d'acceptation :**

- Le formulaire demande la ville de départ, la ville de retour, les dates et heures de début et de retour, ainsi que la catégorie du véhicule.
- Les lieux sont valides et les champs obligatoires sont renseignés.
- La date et l'heure de retour sont après la date et l'heure de départ.
- La catégorie demandée correspond à la nomenclature ACRISS.
- Les résultats correspondent aux critères saisis et aux offres disponibles sur la période.
- Les dates et heures sont interprétées dans le fuseau horaire de l'agence concernée.

#### US#06 - Consulter le détail d'une offre

En tant que client, je veux consulter le détail d'une offre, afin de connaître les conditions et le montant à payer avant de réserver.

**Critères d'acceptation :**

- Le détail présente les lieux, les agences, les dates et heures, la catégorie du véhicule et le tarif.
- Les conditions applicables et les frais connus sont affichés.
- Le montant total attendu, sa devise et les taxes ou frais inclus ou exclus sont compréhensibles.

### Epic 3 - Réservation, paiement et suivi

Objectif : couvrir le parcours de réservation jusqu'au paiement et à la gestion de l'état de la réservation, en respectant les règles de disponibilité, de sécurité et d'idempotence.

#### US#07 - Réserver une offre

En tant que client authentifié, je veux réserver une offre disponible, afin de planifier ma location.

**Critères d'acceptation :**

- Le parcours récapitule l'offre sélectionnée avant le paiement.
- Le tarif et les conditions applicables sont présentés avant le paiement.
- La réservation est confirmée uniquement après réception d'un paiement accepté.
- Une référence de réservation est affichée après confirmation.
- Un échec de paiement ne confirme pas la réservation et ne crée pas de doublon lors d'une nouvelle tentative.

#### US#08 - Payer une réservation

En tant que client authentifié, je veux payer ma réservation via un parcours sécurisé, afin de confirmer ma location sans communiquer mes données bancaires à l'application.

**Critères d'acceptation :**

- Le paiement est redirigé vers le prestataire externe.
- L'application ne stocke pas les données bancaires sensibles.
- Le client est informé de l'acceptation, du refus ou de l'échec du paiement.
- Un paiement accepté déclenche la confirmation de la réservation.

#### US#09 - Consulter ses réservations

En tant que client authentifié, je veux consulter mes réservations, afin de suivre mes locations passées, en cours et à venir.

**Critères d'acceptation :**

- L'historique distingue les réservations en cours ou à venir, passées et annulées lorsqu'elles sont conservées.
- Chaque réservation affiche sa référence, son état, les lieux et dates, la catégorie, le montant et les actions autorisées.

#### US#10 - Modifier une réservation

En tant que client authentifié, je veux modifier une réservation éligible, afin d'adapter ma location à un changement de besoin.

**Critères d'acceptation :**

- Une modification est possible au moins 48 heures avant le début de la location.
- Les éléments modifiables et les conséquences tarifaires sont affichés avant confirmation.
- La disponibilité est vérifiée à nouveau et le montant est recalculé si nécessaire.
- Une demande effectuée dans les 48 heures précédant le début est refusée avec un message explicite.

#### US#11 - Annuler une réservation

En tant que client authentifié, je veux annuler une réservation, afin de mettre fin à une location dont je n'ai plus besoin.

**Critères d'acceptation :**

- Les conditions d'annulation sont affichées avant confirmation.
- Pour une annulation effectuée moins d'une semaine avant le début de la location, le remboursement est limité à 25 % du montant total de la réservation, sous réserve des règles complémentaires validées.
- Le montant et le statut du remboursement sont enregistrés et consultables dans le détail de la réservation.

### Epic 4 - Support client et conversations

Objectif : assurer la communication en temps réel entre le client et le support ou l'agence, sans interrompre l'expérience utilisateur.

#### US#12 - Ouvrir une conversation

En tant que client authentifié, je veux ouvrir une conversation avec le support ou une agence, afin d'obtenir de l'aide sur ma demande ou ma réservation.

**Critères d'acceptation :**

- Le client peut démarrer une conversation depuis son espace client.
- Il est informé du temps d'attente estimé avant d'être pris en ligne.

#### US#13 - Échanger en temps réel

En tant que client authentifié, je veux envoyer et recevoir des messages sans recharger la page, afin d'échanger efficacement avec un conseiller.

**Critères d'acceptation :**

- Le client peut consulter ses conversations ouvertes et clôturées.
- Les messages apparaissent dans l'ordre chronologique avec leur heure et leur état de remise.
- Un message vide ne peut pas être envoyé.

### Epic 5 - Intégration agence et API métier

Objectif : exposer un accès métier sécurisé aux applications d'agence et garantir l'authentification, l'autorisation, la traçabilité et la validation des opérations.

#### US#14 - Gérer les ressources via l'API

En tant qu'application d'agence, je veux consulter et gérer les ressources métier autorisées, afin d'utiliser les données de l'application centrale.

**Critères d'acceptation :**

- L'API couvre les utilisateurs et profils, agences, offres, réservations, paiements et remboursements selon les droits, ainsi que les catégories de véhicules.
- Les opérations de création, consultation unitaire et en liste, modification et suppression ou désactivation sont disponibles lorsqu'elles sont pertinentes.
- Les listes prennent en charge la pagination, le filtrage et le tri.

#### US#15 - Sécuriser les opérations de l'API

En tant que responsable d'une application d'agence, je veux que chaque appel API soit authentifié, autorisé et traçable, afin de protéger les données et les opérations métier.

**Critères d'acceptation :**

- Les accès non autorisés sont refusés et journalisés.
- Les droits de l'appelant sont vérifiés pour chaque opération.
- Les données reçues sont validées et les erreurs sont explicites.
- Les transitions de statut métier sont cohérentes et les opérations critiques sont traçables.
- Le contrat documente l'authentification, les ressources, les formats, les états, les erreurs, les versions, les limites et la reprise sur erreur.

### Tableau récapitulatif des US

| Epic | ID | Acteur | User story | 
| --- | --- | --- | --- | 
| Epic 1 | US#01 | Client authentifié | Consulter son profil |
| Epic 1 | US#02 | Client authentifié | Modifier ses informations personnelles |
| Epic 1 | US#03 | Client authentifié | Demander la suppression de son compte |
| Epic 2 | US#04 | Client | Consulter et rechercher les agences |
| Epic 2 | US#05 | Client | Rechercher une offre |
| Epic 2 | US#06 | Client | Consulter le détail d'une offre |
| Epic 3 | US#07 | Client authentifié | Réserver une offre disponible |
| Epic 3 | US#08 | Client authentifié | Payer sa réservation via un parcours sécurisé |
| Epic 3 | US#09 | Client authentifié | Consulter ses réservations |
| Epic 3 | US#10 | Client authentifié | Modifier une réservation |
| Epic 3 | US#11 | Client authentifié | Annuler une réservation |
| Epic 4 | US#12 | Client authentifié | Ouvrir une conversation avec le support ou une agence |
| Epic 4 | US#13 | Client authentifié | Envoyer et recevoir des messages sans recharger la page |
| Epic 5 | US#14 | Application d'agence | Consulter et gérer les ressources métier autorisées |
| Epic 5 | US#15 | Responsable d'une application d'agence | Garantir que chaque appel API est authentifié, autorisé et traçable |

## Règles métier transverses

- Une offre ne peut être réservée que si elle est disponible au moment de la confirmation.
- La confirmation d’une réservation dépend du succès du paiement auprès du prestataire externe.
- Toutes les dates et heures doivent être interprétées selon le fuseau horaire de l’agence concernée et affichées clairement au client.
- Les catégories de véhicules utilisent les codes et la nomenclature ACRISS : https://www.acriss.org/car-codes/.
- Les montants doivent préciser leur devise et, lorsque nécessaire, les taxes et frais inclus ou exclus.
- Les opérations sensibles doivent être associées au compte, à la date et à l’état de l’opération afin de permettre leur suivi.
- Les règles locales peuvent compléter les règles communes, mais ne doivent pas rendre le parcours incohérent ou contradictoire.
- Un message ne peut être consulté que par les participants et les personnes habilitées à traiter la conversation.
- La conversation doit être rattachée au client qui l’a créée et, si nécessaire, à une seule réservation identifiée.
- La clôture d’une conversation ne doit pas supprimer son historique.

## API pour les applications en agence

Une API sécurisée doit être fournie aux applications utilisées en agence.

### Domaines minimaux

L’API doit couvrir au minimum les domaines suivants :

- utilisateurs et profils ;
- agences ;
- offres ;
- réservations ;
- paiements et remboursements, selon les droits accordés ;
- catégories de véhicules.

La gestion des conversations et des messages doit également être accessible aux applications ou outils de support habilités, avec des droits adaptés à la consultation, l’envoi, l’affectation et la clôture.

### Opérations attendues

Pour chaque domaine exposé, l’API doit fournir les opérations CRUD standard lorsque cela est pertinent :

- création ;
- consultation unitaire et en liste ;
- modification ;
- suppression ou désactivation lorsque la suppression physique n’est pas autorisée.

Les opérations doivent contrôler les droits de l’appelant, valider les données reçues, retourner des erreurs explicites et garantir la cohérence des statuts métier. Les opérations critiques, comme la modification ou l’annulation d’une réservation, doivent être traçables.

### Contrat d’API

Le contrat d’API doit documenter :

- l’authentification et l’autorisation ;
- les ressources et leurs identifiants ;
- les champs obligatoires et les formats ;
- les états et transitions autorisés ;
- les codes d’erreur ;
- la pagination, le filtrage et le tri des listes ;
- la gestion des versions ;
- les règles de limitation et de reprise sur erreur.

## Exigences non fonctionnelles

### Sécurité

- Toutes les communications doivent utiliser une version actuelle et sécurisée de TLS.
- Les mots de passe doivent être stockés avec un algorithme de hachage moderne et adapté, jamais en clair ni avec SHA-1.
- Les secrets applicatifs doivent être externalisés dans un gestionnaire de secrets ou un mécanisme équivalent, avec rotation prévue.
- Les données bancaires ne doivent pas être stockées par l’application.
- Les accès à l’API doivent être authentifiés, autorisés par rôle et journalisés.
- Les actions sensibles, notamment la suppression du compte, doivent demander une réauthentification ou une preuve équivalente.
- Les dépendances et composants utilisés doivent faire l’objet d’un suivi de vulnérabilités.
- Les échanges du tchat doivent être chiffrés pendant leur transport et les accès aux conversations doivent être contrôlés et journalisés.

### Disponibilité et résilience

- Le service doit être déployable de façon reproductible et vérifiable.
- Les composants critiques doivent éviter le point unique de défaillance lorsque cela est compatible avec le périmètre de la V1.
- Les données doivent être sauvegardées automatiquement.
- Les procédures de restauration doivent être documentées et testées régulièrement.
- Les erreurs survenant lors des pics saisonniers doivent être surveillées et signalées.

Les objectifs chiffrés de disponibilité, de temps de reprise et de perte de données admissible doivent être validés par le métier et l’exploitation avant la mise en production.

### Internationalisation et accessibilité

- L’application doit gérer les pays, devises, langues et fuseaux horaires nécessaires au déploiement international.
- Les formats de date, d’heure et d’adresse doivent être adaptés au pays concerné.
- Les messages d’erreur doivent être compréhensibles et localisables.
- L’interface du tchat doit rester utilisable lorsque la connexion est temporairement interrompue et permettre de distinguer les messages envoyés des messages en échec.

### Accessibilité aux personnes en situation de handicap

L'application doit être conçue pour être utilisable par les personnes en situation de handicap, au moyen de technologies d'assistance et de différentes modalités d'interaction.

Le référentiel ou la norme d'accessibilité applicable doit être validé avant le début du développement. Il pourra s'agir, par exemple, du Référentiel général d'amélioration de l'accessibilité (RGAA), des Web Content Accessibility Guidelines (WCAG) ou d'un référentiel interne validé par l'entreprise.

Le référentiel retenu devra définir les exigences concernant notamment la navigation au clavier, les lecteurs d'écran, les contrastes, les formulaires, les contenus multimédias, les messages d'erreur et les interfaces en temps réel.

Le niveau de conformité attendu, les méthodes d'évaluation et les critères d'acceptation devront être précisés dans les spécifications techniques avant la mise en production.

### Observabilité et traçabilité

Le système doit fournir des journaux et indicateurs permettant de suivre :

- les recherches et réservations ;
- les paiements et remboursements ;
- les modifications et annulations ;
- les appels API et leurs erreurs ;
- les ouvertures de conversations, envois de messages, transferts et clôtures ;
- la disponibilité et les temps de réponse.

Les journaux ne doivent pas contenir de données bancaires ni de mots de passe.

### Impact environnemental et écoconception

Le projet doit prendre en compte ses impacts environnementaux directs et indirects sur l'ensemble de son cycle de vie. La centralisation des applications peut réduire la duplication des infrastructures, des données et des opérations de maintenance, mais elle ne constitue pas à elle seule une garantie de réduction des émissions.

Le système doit :

- limiter le poids des pages, des réponses API et des contenus échangés afin de réduire la consommation de données et d'énergie ;
- éviter le chargement ou le traitement de ressources non nécessaires au parcours de l'utilisateur ;
- privilégier une architecture et un hébergement permettant d'adapter les ressources consommées à la charge réelle ;
- limiter la durée de conservation des données, des journaux et des sauvegardes au strict nécessaire ;
- mesurer régulièrement le poids des principaux parcours, le volume de données transférées et la consommation des infrastructures lorsque ces données sont disponibles ;

Les indicateurs et objectifs chiffrés d'écoconception, notamment le poids maximal des pages, le volume maximal de données transférées et les durées de conservation, doivent être définis avant la mise en production. L'impact indirect lié à l'augmentation potentielle de l'utilisation des véhicules doit également être pris en compte dans l'évaluation globale du projet.


## Critères d’acceptation principaux

- Un client peut consulter et modifier son profil après authentification.
- La suppression du compte est impossible sans saisie correcte du mot de passe.
- Une recherche valide retourne uniquement des offres correspondant aux lieux, dates, heures et catégorie demandés.
- Une catégorie de véhicule proposée respecte la nomenclature ACRISS.
- Un client peut consulter le détail d’une offre et son montant avant paiement.
- Une réservation n’est confirmée qu’après paiement accepté par le prestataire externe.
- Une réservation peut être retrouvée dans l’historique avec son état et sa référence.
- Une modification est acceptée à plus de 48 heures du début et refusée dans les 48 heures.
- Une annulation à moins de sept jours applique un remboursement de 25 %, sous réserve des règles complémentaires validées.
- Un client authentifié peut ouvrir une conversation et échanger des messages avec un conseiller sans recharger la page.
- Un message envoyé est affiché dans le bon ordre et reste consultable après reconnexion.
- Un échec d’envoi est signalé au client et ne fait pas croire que le message a été transmis.
- Un utilisateur non habilité ne peut pas consulter une conversation qui ne lui est pas destinée.
- Une application d’agence peut consulter et modifier les ressources autorisées par l’API selon ses droits.
- Les accès non autorisés à l’API sont refusés et journalisés.
- Une sauvegarde et une restauration peuvent être exécutées selon la procédure prévue.

## Points à arbitrer avant réalisation

- création de compte, authentification et récupération du mot de passe ;
- langues et pays couverts dans la première version ;
- devise et affichage des taxes et frais ;
- règles d’annulation à plus de sept jours ;
- comportement en cas de réservation future lors de la suppression d’un compte ;
- champs personnels obligatoires pour une réservation ;
- liste exacte des éléments modifiables dans une réservation ;
- canaux de notification ;
- durée de conservation des données et des journaux ;
- objectifs chiffrés de disponibilité, performance, reprise et sauvegarde ;
- rôles et permissions détaillés des applications en agence ;
- politique de gestion des conflits entre les actions du client et celles d’une agence.
- horaires de disponibilité du tchat et comportement en dehors de ces horaires ;
- délai cible de réponse du support ;
- durée de conservation des conversations et des messages ;
- règles de transfert, d’affectation et de clôture des conversations ;
- pièces jointes, accusés de lecture et notifications hors ligne ;
- outil utilisé par les conseillers pour traiter les conversations.
