### POST Inventory
Ce point d'API permet d'ajouter une nouvelle entrée à l'inventaire, en fournissant les détails suivant de l'article via le DTO Inventory :
- **itemCode** : Le code de l'article (String)
- **name** : Le nom de l'article (String)
- **quantity** : La quantité de l'article (int)
- **unitPrice** : Le prix unitaire de l'article (double)
- **reorderLevel** : Le niveau de réapprovisionnement de l'article (int)
L'element lastRestocked sera mis au jour automatiquement à la date et l'heure actuelles.

### GET Inventory
Ce point d'API permet de récupérer la liste de tous les articles de l'inventaire. Il renvoie une liste d'objets InventoryDTO, chacun contenant les détails suivants :
- **itemCode** : Le code de l'article (String)
- **name** : Le nom de l'article (String)
- **quantity** : La quantité de l'article (int)
- **unitPrice** : Le prix unitaire de l'article (double)
- **reorderLevel** : Le niveau de réapprovisionnement de l'article (int)
- **lastRestocked** : La date et l'heure de la dernière mise à jour de l'article (LocalDateTime)

### GET Inventory/{itemCode}
Ce point d'API permet de récupérer les détails d'un article spécifique de l'inventaire en fournissant le code de l'article dans l'URL. Il renvoie un objet InventoryDTO contenant les détails suivants :
- **itemCode** : Le code de l'article (String)  
- **name** : Le nom de l'article (String)
- **quantity** : La quantité de l'article (int)
- **unitPrice** : Le prix unitaire de l'article (double)
- **reorderLevel** : Le niveau de réapprovisionnement de l'article (int)
- **lastRestocked** : La date et l'heure de la dernière mise à jour de l'article (LocalDateTime)

### PUT Inventory/{itemCode}
Ce point d'API permet de mettre à jour les détails d'un article spécifique de l'inventaire en fournissant le code de l'article dans l'URL et les nouveaux détails via le DTO Inventory. Il met à jour les champs suivants :
- **name** : Le nom de l'article (String)
- **quantity** : La quantité de l'article (int)
- **unitPrice** : Le prix unitaire de l'article (double)
- **reorderLevel** : Le niveau de réapprovisionnement de l'article (int)
- **lastRestocked** : La date et l'heure de la dernière mise à jour de l'article (LocalDateTime)
Si le cout unitaire est mis à jour alors une entrée dans PriceHistory doit être crée pour enregistrer ce changement de prix.

### DELETE Inventory/{itemCode}
Ce point d'API permet de supprimer un article spécifique de l'inventaire en fournissant le code de l'article dans l'URL. Il supprime l'article correspondant de la base de données.
Take into account that the SupplierInvoiceDetails table must be deleted first before deleting the Inventory table.

### GET Inventory/PriceHistory/{itemCode}
Ce point d'API permet de récupérer l'historique des prix d'un article spécifique de l'inventaire en fournissant le code de l'article dans l'URL. Il renvoie une liste d'objets PriceHistoryDTO, chacun contenant les détails suivants :
- **oldPrice** : L'ancien prix de l'article (double)
- **newPrice** : Le nouveau prix de l'article (double)
- **changeDate** : La date et l'heure de la mise à jour du prix (LocalDateTime)


Sur l'intégralité de ces poits API, il est important de respecter la séparation des résponsabilitées, InventoryController appel le service uniquement et renvoie les données, le service peut utiliser SupplierInvoiceDAO et InventoryDAO
