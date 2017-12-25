# -*- coding: utf-8 -*-
import os
from flask import Flask


# Объявляем объект приложения
app = Flask(__name__)
app.config.from_object(__name__)    #загружаем стандартный конфиг

basedir = os.path.abspath(os.path.dirname(__file__))

# Переопределяем поля конфига
app.config.update(dict(
    SQLALCHEMY_USERS_DATABASE_URI ='sqlite:///' + os.path.join(basedir, 'users.db'),
    SQLALCHEMY_APP_DATABASE_URI ='sqlite:///' + os.path.join(basedir, 'data.db'),
    DEBUG=True,                                         #вывод отладочных сообщений в консоль
    SECRET_KEY='uLneverGuessITfuCkyOuaSShole',          #секретный ключ для сессий
    USERNAME='admin',                                   #ну тут только конченный не поймет
    PASSWORD='trendly'
))



from db import db_users_session, db_vk_session
import views


@app.teardown_appcontext
def shutdown_session(exception=None):
    db_users_session.remove()
    db_vk_session.remove()