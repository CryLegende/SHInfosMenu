<div align="center">

<img src="assets/logo.png" alt="SkyHand" width="140">

# SHInfosMenu

*Ouvre un menu d'informations sur un joueur.*

<img src="https://img.shields.io/badge/Paper-26.3-f2a900?style=for-the-badge" alt="Paper 26.3"> <img src="https://img.shields.io/badge/Java-25-e76f00?style=for-the-badge" alt="Java 25"> <img src="https://img.shields.io/badge/SkyHand-Opale-3aa655?style=for-the-badge" alt="SkyHand Opale">

</div>

---

## Présentation

Ouvre un menu d'informations sur un joueur.

Utilisé sur : **Opale**.

## Dépendances

- Obligatoires : `PlaceholderAPI`
- Optionnelle : `SHSkins` (tête du joueur avec son skin dans le menu, sinon tête sans texture : Steve ou Alex)

## Commandes

| Commande | Alias | Description | Permission |
|---|---|---|---|
| `/infos <joueur>` | — | Ouvre le menu d'informations pour un joueur. Joueurs seulement. | `shinfosmenu.use` |
| `/shinfosmenu reload` | — | Recharge `config.yml`, `menu.yml` et `messages.yml`. Marche aussi depuis la console. | `shinfosmenu.admin.reload` |

## Permissions

| Permission | Description | Par défaut |
|---|---|---|
| `shinfosmenu.use` | Ouvrir le menu avec `/infos` (accordée au groupe `default`) | `op` |
| `shinfosmenu.admin` | Toutes les commandes d'administration (parent de la suivante) | `op` |
| `shinfosmenu.admin.reload` | Recharger la configuration | `false` |

## Configuration

Fichiers par défaut repris du serveur (secrets remplacés par `CHANGE_ME`). Cliquez sur un fichier pour l'ouvrir :

<pre>
plugins/SHInfosMenu/
├── <a href="src/main/resources/config.yml">config.yml</a>
├── <a href="src/main/resources/menu.yml">menu.yml</a>
└── <a href="src/main/resources/messages.yml">messages.yml</a>
</pre>

Au premier démarrage avec `messages.yml`, le fichier est créé à partir de la section `messages` de l'ancien `config.yml` (textes déjà en place), complétée par ceux du jar ; cette section n'est plus lue ensuite.

## Compilation

Java 25 et Maven :

```bash
mvn clean package
```

Le jar est généré dans `target/`.

SHSkins doit être installé dans le dépôt Maven local (`mvn install` dans `SHSkins-Velocity`).

---

<div align="center">

<sub>Développé par <b>Cry_Legende</b> pour le serveur <b>SkyHand</b> · Tous droits réservés</sub>

</div>
