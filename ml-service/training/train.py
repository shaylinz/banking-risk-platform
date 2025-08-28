import os, json
import pandas as pd
import joblib
from sklearn.impute import SimpleImputer
from xgboost import XGBClassifier

DATA = os.path.join(os.path.dirname(__file__), '..', 'data', 'cs-training.csv')
ART  = os.path.join(os.path.dirname(__file__), '..', 'artifacts')
os.makedirs(ART, exist_ok=True)

if not os.path.exists(DATA):
    raise FileNotFoundError(f"Could not find dataset at {DATA}")

# 1) Load data
df = pd.read_csv(DATA)

# Some versions include an unnamed index column—drop it if present
if df.columns[0].lower().startswith('unnamed'):
    df = df.drop(columns=[df.columns[0]])

# 2) Target / features
y = df['SeriousDlqin2yrs']
X = df.drop(columns=['SeriousDlqin2yrs'])

# 3) Median imputation (trees don't need scaling)
imp = SimpleImputer(strategy='median')
X_imp = imp.fit_transform(X)

# 4) Train XGBoost (solid defaults)
model = XGBClassifier(
    n_estimators=300, max_depth=4, learning_rate=0.08,
    subsample=0.9, colsample_bytree=0.9,
    eval_metric='logloss', n_jobs=4, random_state=42
)
model.fit(X_imp, y)

# 5) Save artifacts
joblib.dump(model, os.path.join(ART, 'model.joblib'))
joblib.dump(imp,   os.path.join(ART, 'imputer.joblib'))
with open(os.path.join(ART, 'features.json'), 'w') as f:
    json.dump(list(X.columns), f)

print("Saved model + imputer + features.")
