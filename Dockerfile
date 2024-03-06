FROM clojure

WORKDIR /home/app/

RUN apt update && apt install nodejs npm -y

COPY ./package.json .

RUN npm i

COPY . .

RUN npm run tailwind
RUN npm run build

EXPOSE 3000

CMD ["npm", "run", "start"]
