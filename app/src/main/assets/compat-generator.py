# Helper script to generate compatibility data skeleton

PLANTS = [
    "broccoli",
    "garlic",
    "onion",
    "beetroot",
    "bush_beans",
    "string_beans",
    "sweet_peas",
    "strawberry",
    "kohlrabi",
    "cabbage",
    "cauliflower",
    "kale",
    "dill",
    "corn",
    "chard",
    "watermelon",
    "carrot",
    "fruit_trees",
    "bell_pepper",
    "parsnip",
    "chives",
    "parsley",
    "leek",
    "tomato",
    "rhubarb",
    "radish",
    "lettuce",
    "spinach",
    "pumpkin",
    "cucumber",
    "celery",
    "potato"
]

compatibility = []

for plant1 in PLANTS:
    for plant2 in PLANTS:
        compatibility.append({
            "plantId": plant1,
            "otherPlantId": plant2,
            "type": "NEUTRAL"
        })

import json
with open("compatibility.json", "w") as f:
    json.dump(compatibility, f, indent=2)