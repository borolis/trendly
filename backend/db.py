# -*- coding: utf-8 -*-
from backend import app
from sqlalchemy import create_engine
from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base


users_engine = create_engine(app.config['SQLALCHEMY_USERS_DATABASE_URI'], convert_unicode=True)
db_users_session = scoped_session(sessionmaker(autocommit=False,
                                         autoflush=False,
                                         bind=users_engine))
UserBase = declarative_base()
UserBase.query = db_users_session.query_property()


vk_engine = create_engine(app.config['SQLALCHEMY_APP_DATABASE_URI'], convert_unicode=True)
db_vk_session = scoped_session(sessionmaker(autocommit=False,
                                         autoflush=False,
                                         bind=vk_engine))
VKBase = declarative_base()
VKBase.query = db_vk_session.query_property()


def db_init():
    import models
    UserBase.metadata.create_all(bind=users_engine)
    VKBase.metadata.create_all(bind=vk_engine)