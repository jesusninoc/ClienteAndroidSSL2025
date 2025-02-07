# 🔐 Generación de Certificados SSL con `keytool`

Este repositorio contiene instrucciones para generar un **almacén de claves PKCS12 (`.p12`)** para un **servidor**, y exportar su **clave pública** para que los clientes puedan validar la conexión SSL.

---

## 📌 **1. Generar el Certificado del Servidor**

Ejecuta el siguiente comando para crear un **almacén PKCS12 (`.p12`)** con una clave privada y su certificado auto-firmado:

```
keytool -genkeypair -alias servidor -keyalg RSA -keysize 2048 -validity 365 \
-keystore servidor_keystore.p12 -storetype PKCS12 -storepass 1234567
```

### 🔹 **Explicación de los parámetros:**
- `-genkeypair` → Genera un par de claves (clave pública y privada).
- `-alias servidor` → Alias para identificar la clave en el almacén.
- `-keyalg RSA` → Algoritmo de clave (RSA).
- `-keysize 2048` → Tamaño de la clave (2048 bits).
- `-validity 365` → Validez del certificado (1 año).
- `-keystore servidor_keystore.p12` → Nombre del archivo donde se guardará el almacén de claves.
- `-storetype PKCS12` → Tipo de almacén (`.p12` en lugar de JKS).
- `-storepass 1234567` → Contraseña del almacén.

### ✅ **Resultado:**
Se crea el archivo `servidor_keystore.p12`, que contiene:
- 🔐 **Clave privada**
- 🔓 **Clave pública**
- 📜 **Certificado auto-firmado**

---

## 📌 **2. Exportar la Clave Pública del Servidor**

El cliente solo necesita la **clave pública** del servidor para validar la conexión SSL.  
Ejecuta el siguiente comando para exportarla:

```
keytool -export -alias servidor -keystore servidor_keystore.p12 -file servidor_publico.cer -storepass 1234567
```

### 🔹 **Explicación de los parámetros:**
- `-export` → Exporta un certificado.
- `-alias servidor` → Alias del certificado dentro del almacén.
- `-keystore servidor_keystore.p12` → Archivo donde está almacenada la clave pública y privada.
- `-file servidor_publico.cer` → Nombre del archivo de salida con la clave pública.
- `-storepass 1234567` → Contraseña del almacén.

### ✅ **Resultado:**
Se genera el archivo `servidor_publico.cer`, que contiene:
- 🔓 **Clave pública del servidor (en formato X.509)**
- ❌ **No incluye la clave privada**
