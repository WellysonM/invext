# Invext

API Rest desenvolvida para concluir teste tecnico da Ubots.

## 🚀 Começando

Essas instruções permitirão que você execute o projeto sem dificuldades;

### 📋 Pré-requisitos

Java 22. Pode ser feito o download neste [link](https://www.oracle.com/java/technologies/javase/jdk22-archive-downloads.html).

Intellij IDEA. Pode ser feito o download neste [link](https://www.jetbrains.com/pt-br/idea/)

🔧 Instalação

Após clonar o projeto localmente é necessário executar o install do maven atraves do intellij ou usando o comando:

```
  - install -f pom.xml
```
### 🏗️ Build

Após concluir o install do maven o projeto está pronto para ser executado, shit + f10 na classe InvextApplication.java

### ⚙️ Consumindo a API Rest

O projeto possui dois endpoint:

```shell
  # Solicitar atendimento
  
  - POST: localhost:3000/api/requests/distribute
    - Request Param: requestType
        - Valores esperados: Card Issues, Loan Application e Other Issues
  
  # Finalizar atendimento
  
  - POST: localhost:3000/api/requests/finish
    - Request Param: requestType
        - Valores esperados: Card Issues, Loan Application e Other Issues
    
    - Request Param: attendantName    
        - Valores esperados: 
          - Attendant 1 ou Attendant 2 para requestType tipo Card Issues
          - Attendant 3 ou Attendant 4 para requestType tipo Loan Application
          - Attendant 5 ou Attendant 6 para requestType tipo Other Issues
        
```

## 🛠️ Construído com

Ferramentas usadas:

* [Intellij](https://www.jetbrains.com/pt-br/idea/) - Editor de código fonte

## 🖇️ Colaborando

* **Wellyson Marques** - *Backend* - [Dev](https://github.com/WellysonM)
