from asciimatics.screen import Screen
from asciimatics.scene import Scene
from asciimatics.widgets import Frame, Layout, ListBox, Divider, Text, Button, Widget
from asciimatics.exceptions import ResizeScreenError, NextScene, StopApplication
import sys, os, subprocess


basedir = os.path.abspath(os.path.dirname(__file__))
buffer = None


class MainMenu(Frame):
    def __init__(self, screen):
        super(MainMenu, self).__init__(screen,
                                       screen.height * 2 // 3,
                                       screen.width * 2 // 3,
                                       hover_focus=True,
                                       title="Main Menu")
        layout = Layout([100], fill_frame=True)
        self.add_layout(layout)
        layout.add_widget(Button("Backend", self._backend))
        layout.add_widget(Button("Wrapper", self._wrapper))
        layout.add_widget(Button("Exit", self._exit))
        self.fix()

    def _backend(self):
        raise NextScene("Backend")

    def _wrapper(self):
        return

    def _exit(self):
        raise StopApplication()

class BackendMenu(Frame):
    child_process = None
    def __init__(self, screen):
        super(BackendMenu, self).__init__(screen,
                                       screen.height * 2 // 3,
                                       screen.width * 2 // 3,
                                       hover_focus=True,
                                       title="Backend")
        top_layout = Layout([1,1,1,1], fill_frame=True)
        self.add_layout(top_layout)
        top_layout.add_widget(Button("Start", self._start), 0)
        top_layout.add_widget(Button("Kill", self._kill), 1)
        top_layout.add_widget(Button("Cancel", self._cancel), 3)
        output_layout = Layout([100])
        self.add_layout(output_layout)
        output_layout.add_widget(LogWidget("Log"))
        self.fix()

    def _start(self):
        self.child_process = subprocess.Popen(["python", "backend.py"], stdout = self.buffer)
        return

    def _kill(self):
        if (self.child_process != None):
            self.child_process.terminate()
        return

    def _cancel(self):
        raise NextScene("Main")



def ScenePlayer(screen, scene):
    scenes = [
        Scene([MainMenu(screen)], -1, name="Main"),
        Scene([BackendMenu(screen)], -1, name="Backend"),
    ]
    screen.play(scenes, stop_on_resize=True, start_scene=scene)

last_scene = None
while True:
    try:
        Screen.wrapper(ScenePlayer, catch_interrupt=True, arguments=[last_scene])
        sys.exit(0)
    except ResizeScreenError as e:
        last_scene = e.scene