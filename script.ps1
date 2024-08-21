git init
# Создание случайного сообщения коммита
$randomCommitMessage = "Commit_$((Get-Random -Minimum 1000 -Maximum 9999))"

# Добавление всех файлов к индексу
git add --all

# Коммит с случайным сообщением
git commit -m $randomCommitMessage
git branch -M main
git pull --rebase origin main
git push origin main