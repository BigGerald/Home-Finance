# Etapa de build (usando imagem com Maven e JDK integrados)
FROM eclipse-temurin:17-jdk-jammy AS build

# Define diretório de trabalho
WORKDIR /app

# Copia pom.xml e baixa dependências para cache
COPY pom.xml .
RUN apt-get update && apt-get install -y maven && mvn dependency:go-offline

# Copia o restante do código e compila
COPY . .
RUN mvn clean package -DskipTests

# ---------------------------------------------------
# Etapa final: runtime otimizado
# ---------------------------------------------------
FROM eclipse-temurin:17-jre-jammy

# Configura fuso horário para São Paulo
RUN apt-get update && apt-get install -y tzdata \
    && ln -snf /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime \
    && echo "America/Sao_Paulo" > /etc/timezone \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Define diretório de execução
WORKDIR /app

# Copia apenas o .jar compilado da etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que o Spring Boot usará
EXPOSE 80

# Define o comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]
