# Audit de l'existant

## Objet

Ce document synthétise l'audit de l'architecture existante de Your Car Your Way à partir de `docs/entrants/architecture_existante.pdf` et du cahier des charges fonctionnel. Il distingue les éléments observés dans les documents sources, les risques associés et les points à confirmer avant la conception détaillée.

## Synthèse

L'existant est composé de plusieurs applications nationales développées indépendamment. L'architecture est majoritairement monolithique, les données sont isolées par pays et les APIs ne sont pas uniformisées.

La situation permet de bénéficier de plusieurs retours d'expérience techniques, mais elle augmente le coût de maintenance et rend difficile l'homogénéisation des règles de réservation, de la sécurité, de l'exploitation et de l'expérience client.

La priorité de la cible doit être de centraliser les contrats métier et les données nécessaires au parcours client, tout en conservant un mécanisme explicite pour les règles locales. La migration doit être progressive afin de limiter le risque opérationnel.

## Périmètre observé

Le PDF décrit les environnements suivants :

| Périmètre | Technologie principale | Hébergement | Architecture |
| --- | --- | --- | --- |
| France | Java EE, JSP/JSF | OVH | Monolithe complet |
| Allemagne, Espagne, Italie | Java EE | OVH | Dérivés du produit français |
| Royaume-Uni | PHP Laravel | AWS EC2 | Monolithe isolé |
| Canada | React, Node.js | AWS | Frontend séparé, backend monolithique |
| États-Unis | Angular, Spring Boot | Azure App Services / Containers | Monolithe containerisé |

## Architecture technique

### Constats

- Les applications sont principalement des monolithes web.
- Aucun microservice n'est identifié dans l'existant.
- Les APIs sont limitées, hétérogènes et non unifiées.
- Chaque pays possède sa propre base avec un schéma différent.
- Le partage d'information est absent ou réalisé manuellement.
- Les technologies et les modes d'hébergement varient fortement.
- Les déploiements OVH sont manuels, tandis que les environnements cloud sont plus standardisés.

### Risques

- Duplication de code et divergence progressive des règles métier.
- Difficulté à faire évoluer simultanément les six périmètres.
- Risque d'incohérences entre disponibilités, tarifs, réservations et statuts.
- Coût élevé d'une maintenance multi-technologies.
- Forte dépendance aux procédures manuelles pour les environnements historiques.
- Difficulté à garantir un contrat API stable pour les applications en agence.

## Fiabilité et exploitation

### Données observées

- Disponibilité moyenne annoncée : 97,2 % pour FR/DE/ES/IT, 98,6 % pour UK, 98,1 % pour CA et 98,9 % pour US.
- MTTR annoncé : environ 2 h 45 sur OVH contre 1 h 10 sur AWS/Azure.
- Réussite des déploiements : 82 % sur OVH contre 91 % dans les environnements cloud.
- Stabilisation après mise à jour : 3,4 jours sur FR/DE/ES/IT contre 1,7 jour sur UK/CA/US.
- Charge maximale annoncée : environ 150 requêtes/s sur FR/DE/ES/IT, 250 sur UK, 300 sur CA et 350 sur US.
- Taux d'erreur en période de pointe : jusqu'à 4 % sur FR/DE/ES/IT, 1,5 % sur UK/CA et 0,8 % sur US.

### Risques

- Les performances et la disponibilité ne sont pas homogènes.
- Les pics saisonniers touchent plus fortement les environnements historiques.
- Les sauvegardes historiques sont manuelles et leur restauration n'est pas testée.

## Sécurité

### Données observées

- SHA-1 est utilisé pour les mots de passe en FR/DE/ES/IT.
- bcrypt est utilisé au Royaume-Uni et aux États-Unis ; argon2id est utilisé au Canada.
- TLS 1.0 est encore utilisé en France et en Italie.
- Des secrets sont stockés dans des fichiers de configuration sur OVH.
- Les variables d'environnement cloud ne disposent pas d'une rotation automatisée pour UK/CA.
- Azure Key Vault n'est utilisé que partiellement aux États-Unis.
- Le taux de dépendances avec vulnérabilités connues est plus élevé sur les environnements historiques.

### Risques prioritaires

1. Les mots de passe hachés avec SHA-1 doivent être migrés vers un algorithme moderne et adapté à l'authentification.
2. TLS 1.0 doit être désactivé et remplacé par une version actuelle et sécurisée de TLS.
3. Les secrets doivent être retirés des fichiers de configuration et gérés par un coffre de secrets.
4. Les dépendances doivent être inventoriées, mises à jour et soumises à un contrôle automatisé.
5. La migration des mots de passe doit prévoir un mécanisme de transition sans perte de compte et sans exposition des mots de passe en clair.

## Sauvegarde et résilience

### Données observées

- FR/DE/ES/IT : sauvegarde manuelle quotidienne, restauration non testée.
- UK/CA : snapshots quotidiens AWS, sans tests réguliers signalés.
- US : sauvegardes automatisées Azure, test de restauration tous les 90 jours.
- FR/DE/ES/IT ne disposent pas de réplication des instances applicatives.
- UK/CA disposent d'une réplication partielle.
- US dispose d'une application containerisée, mais sa base n'est pas redondante.

### Actions recommandées

- Définir les objectifs RPO et RTO avant de choisir la cible technique.
- Automatiser les sauvegardes et leur supervision.
- Tester régulièrement la restauration et conserver les résultats.
- Éliminer les points uniques de défaillance selon la criticité des composants.
- Documenter les procédures d'incident et de reprise.

## Conclusion et recommandations

L'existant doit être considéré comme une source de données et de règles à rationaliser, et non comme un socle technique homogène à reproduire tel quel.

Les recommandations sont les suivantes :

- définir un modèle métier et un contrat API communs avant la migration ;
- choisir une architecture cible déployable de manière reproductible ;
- isoler les règles locales dans des paramètres ou des modules identifiés ;
- mettre en place une stratégie de migration par domaine et par pays ;
- sécuriser en priorité les mots de passe, TLS, secrets et dépendances ;
- mettre en place des indicateurs communs de disponibilité, performance, erreurs, déploiement, sauvegarde et restauration ;
- conserver une traçabilité des écarts entre l'ancien et le nouveau comportement métier ;
- réaliser un pilote sur un périmètre limité avant la généralisation.

## Points à confirmer

- Règles locales qui doivent être conservées ou supprimées.
- Possibilité d'exporter les données depuis chaque système existant.
- Objectifs RPO, RTO, disponibilité, performance et conservation des données.
- Besoin d'une synchronisation temporaire entre l'ancien et le nouveau système pendant la transition.
