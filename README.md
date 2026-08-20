# Pruebas tecnicas con Docker

Workspace Docker-first para laboratorios independientes de backend, frontend,
bases de datos, debugging y pruebas tecnicas.

Reglas globales:

- Cada laboratorio tendra su propio `compose.yml`, redes y volumenes.
- Los laboratorios se ejecutaran normalmente de uno en uno.
- No se comparte infraestructura entre laboratorios por defecto.
- No se instalan runtimes globales si Docker puede proporcionar el entorno.
- Los secretos locales viven en `.env`, que no se versiona.
- `.env.example` documenta las variables necesarias sin credenciales reales.
- Las bases de datos no publican puertos al host salvo necesidad didactica.

Comandos habituales desde el directorio de un laboratorio:

```text
docker compose up --build
docker compose ps
docker compose logs
docker compose down
```

Los proyectos y recursos Docker legacy existentes fuera de este workspace no
forman parte de esta configuracion y no se modifican desde aqui.

Mantenimiento del host:

- Comprobar el estado con `systemctl is-active docker` y `docker info`.
- Revisar espacio con `docker system df` y `docker buildx du`.
- Revisar logs con `docker compose logs` o `docker logs`.
- Ejecutar normalmente un solo laboratorio y detenerlo con `docker compose down`.
- No usar comandos `prune` globales sin revisar antes contenedores, volumenes e
  imagenes legacy.
- El daemon usa actualmente `json-file` sin rotacion global; cualquier cambio
  requiere evaluar `sudo`, reinicio del daemon y recreacion de contenedores.
