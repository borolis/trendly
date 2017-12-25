# -*- coding: utf-8 -*-
import hashlib, urllib, json, requests
from flask import render_template, url_for, session, request, redirect
from functools import wraps
from backend import app
from db import db_users_session, db_vk_session
from models import User, VKProject, VKPost

def login_required(f):
    @wraps(f)
    def wrap(*args, **kwargs):
        if 'logged_in' in session:
            return f(*args, **kwargs)
        else:
            return redirect(url_for('page_user_login'))
    return wrap

@app.route('/')
@app.route('/index')
def page_index():
    return render_template('index.html')

"""
 -----------------------------------------------------------------------------------------------------------------------
|ПУТИ ОТОБРАЖЕНИЯ ДЛЯ АДМИНИСТРАТОРОВ
 -----------------------------------------------------------------------------------------------------------------------
"""
# Путь страницы логина
@app.route('/alogin', methods=['GET', 'POST'])
def page_admin_login():
    message = None
    if request.method == 'POST':
        if request.form['username'] != app.config['USERNAME'] or request.form['password'] != app.config['PASSWORD']:
            message = 'Wrong credentials'
        else:
            session['logged_in'] = True
            session['role'] = 'admin'
            return redirect(url_for('page_admin_panel'))
    return render_template('admin/login.html', message = message)

@app.route('/apanel')
def page_admin_panel():
    users_list = User.query.all()
    return render_template('admin/panel.html',
                           users = users_list
                           )

@app.route('/apanel/removeuser', methods=['GET', 'POST'])
def remove_user():
    User.query.filter(User.id == request.form['ID']).delete()
    db_users_session.commit()
    return redirect(url_for('page_admin_panel'))

@app.route('/apanel/adduser', methods=['GET', 'POST'])
def add_user():
    new_user = User(username = request.form['newU_username'],
                    firstname = request.form['newU_firstname'],
                    surname = request.form['newU_surname'],
                    email = request.form['newU_email'],
                    password = hashlib.md5(request.form['newU_password']).hexdigest())
    db_users_session.add(new_user)
    db_users_session.commit()
    return redirect(url_for('page_admin_panel'))

"""
 -----------------------------------------------------------------------------------------------------------------------
|ПУТИ ОТОБРАЖЕНИЯ ДЛЯ ПОЛЬЗОВАТЕЛЕЙ
 -----------------------------------------------------------------------------------------------------------------------
"""
# Путь страницы логина
@app.route('/login', methods=['GET', 'POST'])
def page_user_login():
    this_user = None
    message = None
    if request.method == 'POST':
        this_user = User.query.filter(User.username == request.form['username']).first()
        if this_user == None or hashlib.md5(request.form['password']).hexdigest() != this_user.password:
            message = 'Wrong credentials'
        else:
            session['user_id'] = this_user.id
            session['username'] = this_user.username
            session['firstname'] = this_user.firstname
            session['surname'] = this_user.surname
            session['logged_in'] = True
            session['role'] = 'user'
            return redirect(url_for('page_user_dashboard'))
    return render_template('user/login.html',
                           message = message
                           )

# Путь отображения дэшборда
@app.route('/dashboard', methods=['GET', 'POST'])
@login_required
def page_user_dashboard():
    user_projects = VKProject.query.filter(VKProject.user_id == session['user_id'])
    return render_template('user/dashboard.html',
                           title = "Dashboard",
                           user = session['firstname'] + " " + session['surname'],
                           tile_list = user_projects
                           )

headers = {'Content-type': 'application/json',
            'Accept': 'text/plain',
            'Content-Encoding': 'utf-8'}

@app.route('/project/add', methods=['GET', 'POST'])
@login_required
def user_add_project():
    project_id = hash(request.form['name'])
    new_project = VKProject(user_id = session['user_id'],
                            name = request.form['name'],
                            id = project_id
                             )
    db_vk_session.add(new_project)
    db_vk_session.commit()
    data = {
        "type": "new_streaming",
        "keyword": request.form['name'],
        "userId": session['user_id'],
        "projectID": project_id
    }
    requests.post('http://localhost:8080/api', data=json.dumps(data), headers=headers)
    return redirect(url_for('page_user_dashboard'))

@app.route('/project/<project_id>/update')
@login_required
def user_update_project(project_id):
    return

@app.route('/project/<project_id>/remove')
@login_required
def user_remove_project(project_id):
    data = {
        "type": "delete_streaming",
        "userId": session['user_id'],
        "projectId": project_id
    }
    requests.post('http://localhost:8080/api', data=json.dumps(data), headers=headers)
    VKProject.query.filter(VKProject.id == project_id).delete()
    db_vk_session.commit()
    return redirect(url_for('page_user_dashboard'))

@app.route('/settings', methods=['GET', 'POST'])
@login_required
def page_user_settings():
    return render_template('user/settings.html',
                           title = "Settings",
                           user=session['firstname'] + " " + session['surname'],
                           username = session['username'],
                           )

@app.route('/settings/changepsswd', methods=['GET', 'POST'])
@login_required
def user_change_password():
    if (hashlib.md5(request.form['password']).hexdigest() == hashlib.md5(request.form['confirm-password']).hexdigest()):
        User.query.filter(User.id == session['user_id']).update(
            {User.password: hashlib.md5(request.form['password']).hexdigest()}
        )
        db_users_session.commit()
    return redirect(url_for('page_user_settings'))

@app.route('/settings/vkauth', methods=['GET', 'POST'])
def user_vk_auth():
    return


"""
 -----------------------------------------------------------------------------------------------------------------------
|СЛУЖЕБНЫЕ ПУТИ
 -----------------------------------------------------------------------------------------------------------------------
"""

# Путь для осуществления выхода из профиля
@app.route('/logout')
def logout():
    redirect_url = None
    session.pop('logged_in', None)
    if session['role'] == 'admin':
        redirect_url = 'page_admin_login'
    else:
        redirect_url = 'page_user_login'
    session.pop('role', None)
    return redirect(url_for(redirect_url))

def checkAvailibility(url):
    if urllib.urlopen('http://vk.com/' + url).getcode() == 404:
        return False
    else:
        return True