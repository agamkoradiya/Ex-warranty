# Ex-warranty
![demo](https://user-images.githubusercontent.com/54389203/88833442-2bffba00-d1ca-11ea-8907-d752b3962098.gif)
# How to Connect ?
1) Connect your project to firebase
2) Add SHA certificate fingerprints into your firebase
# Storage rules :
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```
# Realtime DB rules :
```
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```
